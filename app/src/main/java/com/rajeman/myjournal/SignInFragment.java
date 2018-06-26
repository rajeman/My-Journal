package com.rajeman.myjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;

import static android.app.Activity.RESULT_OK;
import static com.rajeman.myjournal.MainActivity.RC_SIGN_IN;

public class SignInFragment extends Fragment {

    SignInCancelledNotifier mNotifier;

    public SignInFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mNotifier = (SignInCancelledNotifier) getActivity();
        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .build(),
                RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == RC_SIGN_IN){
            //IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK){
                // open user diary entry
                Toast.makeText(getContext(), "sign_in successful", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();

            }

            else{

                Toast.makeText(getContext(), "sign_in canceled", Toast.LENGTH_SHORT).show();
               mNotifier.onSignInCancelled();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }
}
