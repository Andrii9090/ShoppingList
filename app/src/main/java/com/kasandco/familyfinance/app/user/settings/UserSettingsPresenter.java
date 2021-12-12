package com.kasandco.familyfinance.app.user.settings;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.core.BasePresenter;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import javax.inject.Inject;

public class UserSettingsPresenter extends BasePresenter<UserSettingsView> implements UserSettingsRepository.UserSettingsRepositoryCallback {
    private SharedPreferenceUtil sharedPreference;
    private UserSettingsRepository repository;

    @Inject
    public UserSettingsPresenter(UserSettingsRepository _repository, SharedPreferenceUtil sharedPreferenceUtil) {
        sharedPreference = sharedPreferenceUtil;
        repository = _repository;
    }

    @Override
    public void viewReady(UserSettingsView view) {
        this.view = view;
    }

    @Override
    public void swipeRefresh() {

    }

    public void clickLogOut() {
        view.showLoader();
        repository.deleteAllData(this);
    }

    public void clickBtnSave() {
        String[] userData = view.getUserData();
        String email, oldPassword, newPassword, newPassword2;
        email = userData[0];
        oldPassword = userData[1];
        newPassword = userData[2];
        newPassword2 = userData[3];
        if (!sharedPreference.getSharedPreferences().getString(Constants.EMAIL, "").equals(email)) {
            updateEmail(email, oldPassword);
        }
        if (!newPassword.isEmpty()) {
            changePassword(oldPassword, newPassword, newPassword2);
        }
    }

    private void changePassword(String oldPassword, String newPassword, String newPassword2) {
        if(oldPassword.isEmpty()){
            view.showToast(R.string.text_error_old_password);
        }else {
            if (newPassword.equals(newPassword2)) {
                repository.changePassword(oldPassword, newPassword, newPassword2, this);
            } else {
                view.showToast(R.string.text_error_equals_password);
            }
        }
    }

    private void updateEmail(String email, String oldPassword) {
        if (oldPassword.isEmpty()) {
            view.showToast(R.string.text_error_password_to_change_email);
        } else {
            if (!email.isEmpty()) {
                repository.updateEmail(email, oldPassword, this);
            } else {
                view.showToast(R.string.text_error_empty_email);
            }
        }
    }

    public void clickBtnChangePassword() {
        view.showNewPasswordTextInput();
    }

    @Override
    public void errorToChangeEmail() {
        view.showToast(R.string.text_error_updated_email);
    }

    @Override
    public void emailUpdated() {
        view.showToast(R.string.text_updated_email);
    }

    @Override
    public void dataCleared() {
        view.hideLoader();
        view.startListActivity();
    }

    @Override
    public void passwordChanged(boolean success) {
        if(success){
            view.showToast(R.string.text_password_changed_success);
        }else {
            view.showToast(R.string.text_password_changed_error);
        }
    }
}
