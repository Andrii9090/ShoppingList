package com.kasandco.shoplist.app.user.group;

import android.os.Handler;

import com.kasandco.shoplist.core.Constants;
import com.kasandco.shoplist.network.Requests;
import com.kasandco.shoplist.network.UserNetworkInterface;
import com.kasandco.shoplist.network.model.UserApiModel;
import com.kasandco.shoplist.utils.NetworkConnect;
import com.kasandco.shoplist.utils.SharedPreferenceUtil;


import javax.inject.Inject;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class RepositoryUserGroup {
    private SharedPreferenceUtil sharedPreferenceUtil;
    private NetworkConnect isNetworkConnect;
    private UserGroupRepositoryCallback callback;
    private UserNetworkInterface network;

    @Inject
    public RepositoryUserGroup(Retrofit _retrofit, SharedPreferenceUtil _sharedPref, NetworkConnect _isNetworkConnect) {
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
                public void success(ModelGroup responseObj, Headers headers) {
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
            public void success(ResponseBody responseObj, Headers headers) {
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
        if(uid.equals(sharedPreferenceUtil.getSharedPreferences().getString(Constants.UUID,""))){
            callback.errorUuid();
        }else {
            Handler handler = new Handler();
            Requests.RequestsInterface<ResponseBody> requests = new Requests.RequestsInterface<ResponseBody>() {
                @Override
                public void success(ResponseBody responseObj, Headers headers) {
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

        void errorUuid();
    }
}
