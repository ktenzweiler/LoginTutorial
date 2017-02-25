package com.kodingwithkyle.logintutorial;


public interface LoginContract {
    interface View {
        void showProgress(boolean isShowing);

        void clearErrors();

        void setPasswordError(int error_invalid_password);

        void setEmailError(int error_field_required);

        void showRegistrationDialog();
    }

    interface UserActionsListener {
        void attemptLogin();

        void registerNewUser();

        void attemptRegistration();
    }
}
