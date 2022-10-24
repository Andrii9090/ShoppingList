package com.kasandco.shoplist.app.user.settings;

public interface UserSettingsView {

    void showToast(int text_error_password_to_change_email);

    void startListActivity();

    void showLoader();
    void hideLoader();

    void copyToClipBoard(String uid);

    void showDialog();
}
