package com.kasandco.familyfinance.app.user.group;

import android.os.Handler;

import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.network.Requests;
import com.kasandco.familyfinance.network.UserNetworkInterface;
import com.kasandco.familyfinance.network.model.UIdModel;
import com.kasandco.familyfinance.network.model.UserApiModel;
import com.kasandco.familyfinance.utils.IsNetworkConnect;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;


import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class RepositoryUserGroup {
    private SharedPreferenceUtil sharedPreferenceUtil;
    private IsNetworkConnect isNetworkConnect;
    private UserGroupRepositoryCallback callback;
    private UserNetworkInterface network;

    @Inject
    public RepositoryUserGroup(Retrofit _retrofit, SharedPreferenceUtil _sharedPref, IsNetworkConnect _isNetworkConnect) {
        isNetworkConnect = _isNetworkConnect;
        sharedPreferenceUtil = _sharedPref;
        network = _retrofit.create(UserNetworkInterface.class);
    }

    public void getAllUsers(UserGroupRepositoryCallback _callback) {
        callback = _callback;
        if (isNetworkConnect.isInternetAvailable()) {
            Handler handler = new Handler();
            Requests.RequestsInterface<ModelGroup> callbackResponse = new Requests.RequestsInterface<ModelGroup>() {
                @Override
                public void success(ModelGroup responseObj) {
                    if (responseObj != null && responseObj.getUsers().size() > 0) {
                        if (!responseObj.getUsers().get(0).equals("0")) {
                            handler.post(() -> callback.setAllUsers(responseObj));
                        } else {
                            handler.post(() -> {
                                callback.noMainUser();
                                responseObj.getUsers().remove(0);
                                callback.setAllUsers(responseObj);
                            });
                        }
                    } else {
                        handler.post(() -> callback.errorNoGroupUser());
                    }
                }

                @Override
                public void error() {
                    handler.post(() -> callback.error());
                }

                @Override
                public void noPermit() {
                    callback.error();
                }
            };

            Call<ModelGroup> call = network.getGroup();

            Requests.request(call, callbackResponse);
        } else {
            callback.error();
        }
    }

    public void removeUser(String email) {
        Handler handler = new Handler();
        Requests.RequestsInterface<ResponseBody> callbackResponse = new Requests.RequestsInterface<ResponseBody>() {
            @Override
            public void success(ResponseBody responseObj) {
                handler.post(() -> callback.removedUser(email, true));
            }

            @Override
            public void error() {
                handler.post(() -> callback.removedUser(email, false));
            }

            @Override
            public void noPermit() {
                callback.error();
            }
        };

        Call<ResponseBody> call = network.removeFromGroup(new UserApiModel(email));

        Requests.request(call, callbackResponse);
    }

    public void addNewUserToGroup(String uid) {
        Handler handler = new Handler();
        Requests.RequestsInterface<ResponseBody> requests = new Requests.RequestsInterface<ResponseBody>() {
            @Override
            public void success(ResponseBody responseObj) {
                handler.post(() -> {
                    getAllUsers(callback);
                });
            }

            @Override
            public void error() {
                handler.post(() -> {
                    callback.error();
                });
            }

            @Override
            public void noPermit() {
                callback.error();
            }
        };

        Call<ResponseBody> call = network.addToGroup(uid);
        Requests.request(call, requests);
    }

    public boolean isRegisterUser() {
        return sharedPreferenceUtil.isLogged();
    }

    public String getUserEmail() {
        return sharedPreferenceUtil.getSharedPreferences().getString(Constants.EMAIL, null);
    }


    public interface UserGroupRepositoryCallback {
        void setAllUsers(ModelGroup group);

        void removedUser(String email, boolean isRemoved);

        void error();

        void errorNoGroupUser();

        void noMainUser();
    }
}
