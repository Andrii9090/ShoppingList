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

import java.util.HashMap;

import javax.inject.Inject;

import okhttp3.ResponseBody;
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
                            } else {
                                saveSettingsToServer();
                            }
                        }

                        @Override
                        public void error() {
                            handler.post(() -> {
                                callback.logged(true, response.body().getToken());
                            });
                        }

                        @Override
                        public void noPermit() {

                        }
                    };
                    Requests.request(callSettings, callbackResponse);
                } else {
                    handler.post(() -> {
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


    public void saveSettingsToServer() {
        Requests.RequestsInterface<ResponseBody> callbackResponse = new Requests.RequestsInterface<ResponseBody>() {
            @Override
            public void success(ResponseBody responseObj) {
            }
            @Override
            public void error() {
            }

            @Override
            public void noPermit() {

            }
        };
        HashMap<String, String> settings = new HashMap<>();
        settings.put("theme", String.valueOf(sharedPreference.getSharedPreferences().getInt(Constants.COLOR_THEME, Constants.THEME_DEFAULT)));
        settings.put("currency", sharedPreference.getSharedPreferences().getString(Constants.SHP_DEFAULT_CURRENCY, Constants.DEFAULT_CURRENCY_VALUE));
        UserSettingsApiModel settingsBody = new UserSettingsApiModel(settings);
        Call<ResponseBody> call = network.saveSettings(settingsBody, sharedPreference.getDeviceId());
        Requests.request(call, callbackResponse);
    }

    public void saveToken(String token, String email) {
        sharedPreference.getEditor().putString(Constants.TOKEN, token).apply();
        sharedPreference.getEditor().putString(Constants.EMAIL, email).apply();
    }

    public interface LoginCallback {
        void logged(boolean isLogged, String token);
    }
}
