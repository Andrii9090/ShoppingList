package com.kasandco.familyfinance.app.user.settings;

import com.kasandco.familyfinance.core.AppDataBase;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.network.UserNetworkInterface;
import com.kasandco.familyfinance.network.model.ModelChangePassword;
import com.kasandco.familyfinance.network.model.ModelUpdateEmail;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSettingsRepository {
    private SharedPreferenceUtil sharedPreference;
    private AppDataBase appDataBase;
    private UserNetworkInterface network;

    private String deviceId;

    public UserSettingsRepository(SharedPreferenceUtil _sharedPreferenceUtil, AppDataBase _appDataBase, UserNetworkInterface _network) {
        sharedPreference = _sharedPreferenceUtil;
        appDataBase = _appDataBase;
        network = _network;
    }

    public void deleteAllData(UserSettingsRepositoryCallback callback) {
        Call<ResponseBody> call = network.clearAllHistory(deviceId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    sharedPreference.getEditor().putString(Constants.EMAIL, null).apply();
                    sharedPreference.getEditor().putString(Constants.TOKEN, null).apply();
                    new Thread(()->appDataBase.clearAllTables()).start();
                    callback.dataCleared();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    public void updateEmail(String emailNew, String password, UserSettingsRepositoryCallback callback) {
        Call<ResponseBody> call = network.updateEmail(new ModelUpdateEmail(sharedPreference.getSharedPreferences().getString(Constants.EMAIL, ""), emailNew, password));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    callback.emailUpdated();
                    sharedPreference.getEditor().putString(Constants.EMAIL, emailNew).apply();
                }else {
                    callback.errorToChangeEmail();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.errorToChangeEmail();
            }
        });
    }

    public void changePassword(String oldPassword, String newPassword, String newPassword2, UserSettingsRepositoryCallback callback) {
        Call<ResponseBody> call = network.changePassword(new ModelChangePassword(newPassword, newPassword2, oldPassword));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    callback.passwordChanged(true);
                }else {
                    callback.passwordChanged(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.passwordChanged(false);
            }
        });
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public interface UserSettingsRepositoryCallback{
        void errorToChangeEmail();
        void emailUpdated();
        void dataCleared();
        void passwordChanged(boolean success);
    }
}
