package com.kasandco.familyfinance.app.user;

import android.os.Handler;

import com.google.gson.Gson;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.network.Requests;
import com.kasandco.familyfinance.network.UserNetworkInterface;
import com.kasandco.familyfinance.network.model.UserSettingObj;
import com.kasandco.familyfinance.network.model.UserSettingsApiModel;
import com.kasandco.familyfinance.network.model.UserTokenApiModel;
import com.kasandco.familyfinance.network.model.UserRegisterApiModel;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository {
    private SharedPreferenceUtil sharedPreference;
    private UserNetworkInterface network;

    @Inject
    public LoginRepository(UserNetworkInterface userNetworkInterface, SharedPreferenceUtil sharedPreferenceUtil) {
        network = userNetworkInterface;
        sharedPreference = sharedPreferenceUtil;
    }

    public void login(UserRegisterApiModel user, LoginCallback callback) {
        Call<UserTokenApiModel> call = network.login(user);
        Handler handler = new Handler();
        call.enqueue(new Callback<UserTokenApiModel>() {
            @Override
            public void onResponse(Call<UserTokenApiModel> call, Response<UserTokenApiModel> response) {
                if (response.isSuccessful()) {
                    Call<UserSettingsApiModel> callSettings = network.getSettings("Token " + response.body().getToken(), sharedPreference.getDeviceId());
                    Requests.RequestsInterface<UserSettingsApiModel> callbackResponse = new Requests.RequestsInterface<UserSettingsApiModel>() {
                        @Override
                        public void success(UserSettingsApiModel responseObj) {
                            if (responseObj.getSettings_json() != null) {
                                Gson gson = new Gson();
                                UserSettingObj json = gson.fromJson(responseObj.getSettings_json().toString(), UserSettingObj.class);
                                if (json != null) {
                                    sharedPreference.getEditor().putString(Constants.SHP_DEFAULT_CURRENCY, json.currency).apply();
                                    sharedPreference.getEditor().putInt(Constants.COLOR_THEME, json.theme).apply();
                                }
                                handler.post(() -> {
                                    callback.logged(true, response.body().getToken());
                                });
                            }
                        }

                        @Override
                        public void error() {

                            handler.post(() -> {
                                callback.logged(true, response.body().getToken());
                            });
                        }
                    };
                    Requests.request(callSettings, callbackResponse);
                } else {
                    handler.post(()->{
                        callback.logged(false, null);
                    });
                }
            }

            @Override
            public void onFailure(Call<UserTokenApiModel> call, Throwable t) {
                callback.logged(false, null);
            }
        });
    }

    public void saveToken(String token, String email) {
        sharedPreference.getEditor().putString(Constants.TOKEN, token).apply();
        sharedPreference.getEditor().putString(Constants.EMAIL, email).apply();
    }

    public interface LoginCallback {
        void logged(boolean isLogged, String token);
    }
}
