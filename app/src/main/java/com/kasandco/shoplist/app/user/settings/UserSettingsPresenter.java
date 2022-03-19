package com.kasandco.shoplist.app.user.settings;

import com.kasandco.shoplist.R;
import com.kasandco.shoplist.core.BasePresenter;
import com.kasandco.shoplist.utils.SharedPreferenceUtil;

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
