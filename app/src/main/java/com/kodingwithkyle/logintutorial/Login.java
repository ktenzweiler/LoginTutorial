package com.kodingwithkyle.logintutorial;

import android.support.annotation.NonNull;

/**
 * Created by crunch on 2/14/17.
 */

public final class Login {
    private final String mEmail;
    private final String mPassword;

    public Login(@NonNull String email, @NonNull String password) {
        this.mEmail = email;
        this.mPassword = password;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getPassword() {
        return mPassword;
    }
}
