package com.kasandco.familyfinance.app.user.group;

import com.kasandco.familyfinance.utils.IsNetworkConnect;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Retrofit;

public class RepositoryUserGroup {
    private SharedPreferenceUtil sharedPreferenceUtil;
    private IsNetworkConnect isNetworkConnect;
    private UserGroupRepositoryCallback callback;

    @Inject
    public RepositoryUserGroup(Retrofit _retrofit, SharedPreferenceUtil _sharedPref, IsNetworkConnect _isNetworkConnect){
        isNetworkConnect = _isNetworkConnect;
        sharedPreferenceUtil = _sharedPref;
    }

    public void getAllUsers(UserGroupRepositoryCallback _callback) {
        callback  = _callback;
        List<String> users=new ArrayList<>();
        users.add("mail@jj.ru");
        users.add("mail@ffj.ru");
        users.add("mail@jdff.ru");
        ModelGroup model = new ModelGroup("test", users);
        callback.setAllUsers(model);
    }

    public void removeUser(String email) {
        callback.removedUser(email, true);
    }

    public void addNewUserToGroup(String newUserEmail) {
    }


    public interface UserGroupRepositoryCallback{
        void setAllUsers(ModelGroup group);
        void removedUser(String email, boolean isRemoved);
    }
}
