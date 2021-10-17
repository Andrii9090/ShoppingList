package com.kasandco.familyfinance.app.user.settings;

public interface UserSettingsView {

    String[] getUserData();

    void showToast(int text_error_password_to_change_email);

    void showNewPasswordTextInput();

    void startListActivity();

    String getDeviceId();

    void showLoader();
    void hideLoader();
}
