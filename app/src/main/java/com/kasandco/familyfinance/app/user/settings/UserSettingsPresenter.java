package com.kasandco.familyfinance.app.user.settings;

import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.Status;
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
        repository.deleteAllData(false, this);
    }
    public void clickLogOut(boolean logoutAll) {
        view.showLoader();
        repository.deleteAllData(logoutAll, this);
    }

    @Override
    public void dataCleared() {
        view.hideLoader();
        view.startListActivity();
    }

    @Override
    public void uid(String uid) {
        if(uid!=null){
            view.copyToClipBoard(uid);
            view.showDialog();
        }else {
            view.showToast(R.string.error_copy_uid);
        }
    }

    public void clickBtnCopyUid() {
        repository.getUid(this);
    }
}
