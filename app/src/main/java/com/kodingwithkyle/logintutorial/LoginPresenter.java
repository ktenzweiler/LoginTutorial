package com.kodingwithkyle.logintutorial;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import static android.content.ContentValues.TAG;
import static android.support.test.espresso.core.deps.guava.base.Strings.isNullOrEmpty;
import static android.support.test.internal.util.Checks.checkNotNull;

/**
 * Created by crunch on 2/14/17.
 */

public class LoginPresenter implements LoginContract.UserActionsListener {

    private LoginContract.View mLoginView;
    private LoginActivity mLoginActivity;
    private FirebaseAuth mFirebaseAuth;

    public LoginPresenter(@NonNull LoginActivity loginActivity, FirebaseAuth firebaseAuth) {
        this.mLoginView = checkNotNull(loginActivity, "loginView cannot be null");
        mLoginActivity = loginActivity;
        mFirebaseAuth = firebaseAuth;
    }

    @Override
    public void attemptLogin() {

        //the user is already logged in
        if (mFirebaseAuth.getCurrentUser() != null) {
            mLoginActivity.startActivity(new Intent(mLoginActivity, MainActivity.class));
        } else {
            // Reset errors.
            mLoginView.clearErrors();

            Login login = mLoginActivity.getLogin();
            String email, password;
            email = login.getEmail();
            password = login.getPassword();

            boolean cancel = false;

            // Check for a valid password, if the user entered one.
            if (isNullOrEmpty(password) && !isPasswordValid(password)) {
                mLoginView.setPasswordError(R.string.error_invalid_password);
                cancel = true;
            }

            // Check for a valid email address.
            if (isNullOrEmpty(email)) {
                mLoginView.setEmailError(R.string.error_field_required);
                cancel = true;
            } else if (!isEmailValid(email)) {
                mLoginView.setEmailError(R.string.error_invalid_email);
                cancel = true;
            }

            if (!cancel) {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                mLoginView.showProgress(true);
                AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                mFirebaseAuth.getCurrentUser().linkWithCredential(credential)
                        .addOnCompleteListener(mLoginActivity, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "linkWithCredential:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.d(TAG, "unsuccessfully logged in " + task.getResult());
                                }

                            }
                        });

                mFirebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(mLoginActivity,
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                        if (task.isSuccessful()) {
                                            mLoginActivity.startActivity(new Intent(mLoginActivity, MainActivity.class));
                                            mLoginActivity.finish();
                                        } else {
                                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                                            Toast.makeText(mLoginActivity, R.string.auth_failed
                                                    , Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
            }
        }
    }

    @Override
    public void attemptRegistration() {

        // Reset errors.
        mLoginView.clearErrors();

        Login login = mLoginActivity.getLogin();
        String email, password;
        email = login.getEmail();
        password = login.getPassword();

        boolean cancel = false;

        // Check for a valid password, if the user entered one.
        if (isNullOrEmpty(password) && !isPasswordValid(password)) {
            mLoginView.setPasswordError(R.string.error_invalid_password);
            cancel = true;
        }

        // Check for a valid email address.
        if (isNullOrEmpty(email)) {
            mLoginView.setEmailError(R.string.error_field_required);
            cancel = true;
        } else if (!isEmailValid(email)) {
            mLoginView.setEmailError(R.string.error_invalid_email);
            cancel = true;
        }

        if (!cancel) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginView.showProgress(true);

            Log.d("loginPresenter ", "the email and password" + email + " " + password);
            mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Log.d(TAG, "unsuccessful at creating a new account " + task.getResult().toString());
                                mLoginActivity.showProgress(false);
                            }
                        }
                    });
        }
    }

    @Override
    public void registerNewUser() {
        mLoginView.showRegistrationDialog();
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}
