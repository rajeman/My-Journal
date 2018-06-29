package com.rajeman.myjournal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.signin.SignIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements  SignInCancelledNotifier, JournalEntriesFragment.FabClickListener {

    private FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    public static final int RC_SIGN_IN = 18;
    public static final int RC_SIGN_IN_SUCCESS = 19;
    public static final int RC_SIGN_IN_FAIL = 10;
    private boolean isJustLoggedIn = false;
    String uid;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init firebase fields
        isJustLoggedIn = false;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();

        //set a listener to firebase auth
        mFirebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //we are signed in
                     uid = user.getUid();

                     //if it is a configuration change, don't reload the fragment. Android OS does that
                     if(savedInstanceState == null) {
                         JournalEntriesFragment jFragment = new JournalEntriesFragment();
                         Bundle args = new Bundle();
                         args.putString(getString(R.string.user_uid_key), uid);
                         jFragment.setArguments(args);

                         //mDatabaseReference.child("users").child(uid).push().setValue("this is my first post");
                         getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_frame, jFragment).commitAllowingStateLoss();
                     }
                     else{
                         //savedinstance state is not null and the user just logged in
                         if(isJustLoggedIn){
                             JournalEntriesFragment jFragment = new JournalEntriesFragment();
                             Bundle args = new Bundle();
                             args.putString(getString(R.string.user_uid_key), uid);
                             jFragment.setArguments(args);
                             getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_frame, jFragment).commitAllowingStateLoss();
                         }
                     }
                } else {
                    //we are signed out. try to sign in by calling sign in fragment
                            isJustLoggedIn = true;
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_frame, new SignInFragment()).commitAllowingStateLoss();


                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onSignInCancelled() {

        finish();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onFabClicked() {
        Toast.makeText(this, "fab clicked", Toast.LENGTH_SHORT).show();

        AddEntryFragment addEntryFragment = new AddEntryFragment();
        Bundle args = new Bundle();
        args.putString(getString(R.string.user_uid_key), uid);
        addEntryFragment.setArguments(args);

        //mDatabaseReference.child("users").child(uid).push().setValue("this is my first post");
        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_frame, addEntryFragment).addToBackStack(null).commit();

    }
}
