package com.kasandco.familyfinance.app.user.group;

import com.kasandco.familyfinance.core.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PresenterUserGroup extends BasePresenter<ContractUserGroup.View> implements ContractUserGroup.Presenter, RepositoryUserGroup.UserGroupRepositoryCallback {
    private RepositoryUserGroup repository;
    private String groupName;
    private List<String> users;

    @Inject
    public PresenterUserGroup(RepositoryUserGroup _repository) {
        repository = _repository;
        users = new ArrayList<>();
    }


    @Override
    public void viewReady(ContractUserGroup.View view) {
        this.view = view;
        repository.getAllUsers(this);
    }

    @Override
    public void swipeRefresh() {

    }

    @Override
    public void setAllUsers(ModelGroup group) {
        if (group != null) {
            groupName = group.getGroupName();
            if (users.size() > 0) {
                users.clear();
            }
            users.addAll(group.getUsers());
            view.setGroupName(groupName);
            view.setDataToAdapter(users);
        }
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
    public void removeUser(int position) {
        repository.removeUser(users.get(position));
    }

    public void newUserEmail(String newUserEmail) {
        repository.addNewUserToGroup(newUserEmail);
    }
}
