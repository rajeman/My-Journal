package com.rajeman.myjournal.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rajeman.myjournal.R;
import com.rajeman.myjournal.view.fragments.SignInCancelledNotifier;
import com.rajeman.myjournal.view.fragments.AddEntryFragment;
import com.rajeman.myjournal.view.fragments.JournalEntriesFragment;
import com.rajeman.myjournal.view.fragments.SignInFragment;
import com.rajeman.myjournal.view.fragments.ViewEntryFragment;

/*this is the master and only activity (with exception of firebase authui sign-in activity)*/
/*this activity takes care of authentication and displaying the other pages(fragments)*/

public class MainActivity extends AppCompatActivity
        implements SignInCancelledNotifier, JournalEntriesFragment.FabClickListener, EntriesAdapter.EntryClickNotifier {

    private FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    public static final int RC_SIGN_IN = 18;
    private boolean isJustLoggedIn = false;
    String uid;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

                     //if it is not a configuration change and we are logged in, then display the journal-entries-list
                     if(savedInstanceState == null) {
                         JournalEntriesFragment jFragment = new JournalEntriesFragment();
                         Bundle args = new Bundle();
                         args.putString(getString(R.string.user_uid_key), uid);
                         jFragment.setArguments(args);


                         getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_frame, jFragment,  getString(R.string.entries_fragment_tag)).commitAllowingStateLoss();
                     }
                     //if it is a configuration change, don't reload the fragment. Android OS does that
                     else{
                         //savedinstance state is not null and the user just logged in
                         if(isJustLoggedIn){

                             JournalEntriesFragment jFragment = new JournalEntriesFragment();
                             Bundle args = new Bundle();
                             args.putString(getString(R.string.user_uid_key), uid);
                             jFragment.setArguments(args);
                             getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_frame, jFragment,  getString(R.string.entries_fragment_tag)).commitAllowingStateLoss();
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
//closes the actvity when user cancels sign in. it is called by the signin fragment
        finish();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }
//this is the method that is called when floating action button is clicked on the journal-entries-fragment
//it starts the add-entry-fragment and puts the user uid as an argument
    @Override
    public void onFabClicked() {
        //Toast.makeText(this, "fab clicked", Toast.LENGTH_SHORT).show();

        AddEntryFragment addEntryFragment = new AddEntryFragment();
        Bundle args = new Bundle();
        args.putString(getString(R.string.user_uid_key), uid);
        addEntryFragment.setArguments(args);


        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_frame, addEntryFragment).addToBackStack(null).commit();

    }
//this is the method that is called when an entry is clicked on the journal-entries-list fragment.
// it takes the position of the entry and starts the view-entry fragment. it  puts the position clicked and
// the userId as  arguments

    @Override
    public void onNotify( int position) {

        ViewEntryFragment viewEntryFragment = new ViewEntryFragment();
        Bundle args = new Bundle();
        args.putString(getString(R.string.user_uid_key), uid);
        args.putInt(getString(R.string.adapter_position_key), position);
        viewEntryFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_frame, viewEntryFragment).addToBackStack(null).commit();


    }


}
