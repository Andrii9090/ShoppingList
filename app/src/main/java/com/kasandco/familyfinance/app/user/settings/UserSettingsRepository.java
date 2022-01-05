package com.kasandco.familyfinance.app.user.settings;

import android.os.Handler;

import com.kasandco.familyfinance.core.AppDataBase;
import com.kasandco.familyfinance.core.BaseRepository;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.core.icon.IconModel;
import com.kasandco.familyfinance.network.Requests;
import com.kasandco.familyfinance.network.UserNetworkInterface;
import com.kasandco.familyfinance.network.model.ChangePaswordApiModel;
import com.kasandco.familyfinance.network.model.UIdModel;
import com.kasandco.familyfinance.network.model.UpdateEmailApiModel;
import com.kasandco.familyfinance.utils.IsNetworkConnect;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSettingsRepository extends BaseRepository {
    private SharedPreferenceUtil sharedPreference;
    private AppDataBase appDataBase;
    private UserNetworkInterface network;

    public UserSettingsRepository(SharedPreferenceUtil _sharedPreferenceUtil, AppDataBase _appDataBase, UserNetworkInterface _network, IsNetworkConnect _isNetworkConnect) {
        super(_sharedPreferenceUtil, _isNetworkConnect);
        sharedPreference = _sharedPreferenceUtil;
        appDataBase = _appDataBase;
        network = _network;
    }

    public void deleteAllData(UserSettingsRepositoryCallback callback) {
        Call<ResponseBody> call = network.clearAllHistory(deviceId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    sharedPreference.getEditor().putString(Constants.EMAIL, null).apply();
                    sharedPreference.getEditor().putString(Constants.TOKEN, null).apply();
                    sharedPreference.getEditor().putString(Constants.UUID, null).apply();
                    new Thread(() -> {
                        List<IconModel> icons = appDataBase.getIconDao().getAllIcon();
                        appDataBase.clearAllTables();
                        for (IconModel icon:icons) {
                            appDataBase.getIconDao().insert(icon);
                        }
                    }).start();
                    sharedPreference.getEditor().putInt(Constants.IS_ADDED_ICONS, 0).apply();
                    callback.dataCleared();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    public void updateEmail(String emailNew, String password, UserSettingsRepositoryCallback callback) {
        Call<ResponseBody> call = network.updateEmail(new UpdateEmailApiModel(sharedPreference.getSharedPreferences().getString(Constants.EMAIL, ""), emailNew, password));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    callback.emailUpdated();
                    sharedPreference.getEditor().putString(Constants.EMAIL, emailNew).apply();
                } else {
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
        Call<ResponseBody> call = network.changePassword(new ChangePaswordApiModel(newPassword, newPassword2, oldPassword));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    callback.passwordChanged(true);
                } else {
                    callback.passwordChanged(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.passwordChanged(false);
            }
        });
    }

    public void getUid(UserSettingsRepositoryCallback callback) {
        String uid = sharedPreference.getSharedPreferences().getString(Constants.UUID, null);
        if(uid != null){
            callback.uid(uid);
        }else {
            getUidFromServer(callback);
        }
    }

    private void getUidFromServer(UserSettingsRepositoryCallback callback) {
        Handler handler = new Handler();
        Requests.RequestsInterface<UIdModel> requests = new Requests.RequestsInterface<UIdModel>() {
            @Override
            public void success(UIdModel responseObj) {
                sharedPreference.getEditor().putString(Constants.UUID, responseObj.getUid()).apply();
                handler.post(()->callback.uid(responseObj.getUid()));
            }

            @Override
            public void error() {
                handler.post(()->callback.uid(null));
            }

            @Override
            public void noPermit() {
            }
        };
        Call<UIdModel> call = network.getUid();
        Requests.request(call, requests);
    }

    public interface UserSettingsRepositoryCallback {
        void errorToChangeEmail();

        void emailUpdated();

        void dataCleared();

        void passwordChanged(boolean success);
        void uid(String uid);
    }
}
