package com.kasandco.shoplist.app.settings;


import com.kasandco.shoplist.core.BaseRepository;
import com.kasandco.shoplist.core.Constants;
import com.kasandco.shoplist.network.Requests;
import com.kasandco.shoplist.network.UserNetworkInterface;
import com.kasandco.shoplist.network.model.UserSettingsApiModel;
import com.kasandco.shoplist.utils.NetworkConnect;
import com.kasandco.shoplist.utils.SharedPreferenceUtil;

import java.util.HashMap;

import javax.inject.Inject;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class SettingsRepository extends BaseRepository {
    private UserNetworkInterface userNetworkInterface;

    @Inject
    public SettingsRepository(SharedPreferenceUtil sharedPreferenceUtil, NetworkConnect isNetworkConnect, Retrofit retrofit) {
        super(sharedPreferenceUtil, isNetworkConnect);
        userNetworkInterface = retrofit.create(UserNetworkInterface.class);
    }

    public void saveSettingsToServer(){
        if(isLogged && isNetworkConnect.isInternetAvailable()) {
            Requests.RequestsInterface<ResponseBody> callbackResponse = new Requests.RequestsInterface<ResponseBody>() {
                @Override
                public void success(ResponseBody responseObj, Headers headers) {

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
            Call<ResponseBody> call = userNetworkInterface.saveSettings(settingsBody, sharedPreference.getDeviceId());
            Requests.request(call, callbackResponse);
        }
    }
}
