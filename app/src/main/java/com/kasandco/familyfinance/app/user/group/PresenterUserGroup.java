package com.kasandco.familyfinance.app.user.group;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.core.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PresenterUserGroup extends BasePresenter<ContractUserGroup.View> implements ContractUserGroup.Presenter, RepositoryUserGroup.UserGroupRepositoryCallback {
    private RepositoryUserGroup repository;
    private List<String> users;

    @Inject
    public PresenterUserGroup(RepositoryUserGroup _repository) {
        repository = _repository;
        users = new ArrayList<>();
    }


    @Override
    public void viewReady(ContractUserGroup.View view) {
        this.view = view;
        view.showLoading(true);
        if(repository.isRegisterUser()) {
            repository.getAllUsers(this);
        }else {
            view.showDialogNotRegisterUser();
        }
    }

    @Override
    public void swipeRefresh() {

    }

    @Override
    public void setAllUsers(ModelGroup group) {
        if (group != null) {
            if (users.size() > 0) {
                users.clear();
            }
            users.addAll(group.getUsers());
            view.setDataToAdapter(users);
        }
        view.showLoading(false);
    }

    @Override
    public void removedUser(String email, boolean isRemoved) {
        if (isRemoved) {
            view.userRemoved(email);
        } else {
            view.showToastErrorRemoveUser();
        }
    }

    @Override
    public void error() {
        view.showLoading(false);
        view.showToast(R.string.text_error_load_group);
    }

    @Override
    public void errorNoGroupUser() {
        view.showLoading(false);
        view.showToast(R.string.text_error_no_group_user);
    }

    @Override
    public void noMainUser() {
        view.noMainUser();
    }

    @Override
    public void errorUuid() {
        view.showToast(R.string.text_error_uuid);
    }

    @Override
    public void removeUser(int position) {
        repository.removeUser(users.get(position));
    }

    public void newUserEmail(String userUid) {
        repository.addNewUserToGroup(userUid);
    }

    public void exitFromGroup() {
        String email = repository.getUserEmail();
        if(email!=null) {
            repository.removeUser(email);
        }
    }
}
