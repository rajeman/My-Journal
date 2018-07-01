package com.rajeman.myjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import java.util.Arrays;

import static android.app.Activity.RESULT_OK;
import static com.rajeman.myjournal.MainActivity.RC_SIGN_IN;


/*this fragment takes care of sign-in by starting the firebase-authui activity
* it is a work-around to prevent the sign-in page from continuously popping up when the user presses back button when not singed-in
* it notifies the main activity when sign-in is complete */

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
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK){
                // open user diary entry

                Toast.makeText(getContext(), getString(R.string.sign_in_successful), Toast.LENGTH_SHORT).show();
                //fragment should finish itself
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();


                //auth listener automatically loads the journal-entries-list fragment

            }

            else{

                if (response == null) {
                    // User pressed back button
                    Toast.makeText(getContext(), getString(R.string.sign_in_cancelled), Toast.LENGTH_SHORT).show();
                    mNotifier.onSignInCancelled();

                }

                else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(getContext(), getString(R.string.need_network), Toast.LENGTH_SHORT).show();
                    mNotifier.onSignInCancelled();
                }


            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }
}
