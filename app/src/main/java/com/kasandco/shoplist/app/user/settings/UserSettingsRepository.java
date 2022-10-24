package com.kasandco.shoplist.app.user.settings;

import android.os.Handler;

import com.kasandco.shoplist.core.AppDataBase;
import com.kasandco.shoplist.core.BaseRepository;
import com.kasandco.shoplist.core.Constants;
import com.kasandco.shoplist.core.icon.IconModel;
import com.kasandco.shoplist.network.Requests;
import com.kasandco.shoplist.network.UserNetworkInterface;
import com.kasandco.shoplist.network.model.UIdModel;
import com.kasandco.shoplist.utils.NetworkConnect;
import com.kasandco.shoplist.utils.SharedPreferenceUtil;

import java.util.List;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSettingsRepository extends BaseRepository {
    private SharedPreferenceUtil sharedPreference;
    private AppDataBase appDataBase;
    private UserNetworkInterface network;

    public UserSettingsRepository(SharedPreferenceUtil _sharedPreferenceUtil, AppDataBase _appDataBase, UserNetworkInterface _network, NetworkConnect _isNetworkConnect) {
        super(_sharedPreferenceUtil, _isNetworkConnect);
        sharedPreference = _sharedPreferenceUtil;
        appDataBase = _appDataBase;
        network = _network;
    }

    public void deleteAllData(boolean logoutAll, UserSettingsRepositoryCallback callback) {
        if(logoutAll){
            Call<ResponseBody> call = network.logoutAll();
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        sharedPreference.logout();
                        new Thread(() -> {
                            List<IconModel> icons = appDataBase.getIconDao().getAllIcon();
                            appDataBase.clearAllTables();
                            for (IconModel icon : icons) {
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
        }else {
            Call<ResponseBody> call = network.clearAllHistory(deviceId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        sharedPreference.logout();
                        new Thread(() -> {
                            appDataBase.clearAllTables();
                        }).start();
                        new Thread(()->{
                            List<IconModel> icons = appDataBase.getIconDao().getAllIcon();
                            for (IconModel icon : icons) {
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
            public void success(UIdModel responseObj, Headers headers) {
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

        void dataCleared();

        void uid(String uid);
    }
}
