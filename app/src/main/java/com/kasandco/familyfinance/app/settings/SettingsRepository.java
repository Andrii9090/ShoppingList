package com.kasandco.familyfinance.app.settings;

import android.util.Log;

import com.kasandco.familyfinance.core.BaseRepository;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.network.Requests;
import com.kasandco.familyfinance.network.UserNetworkInterface;
import com.kasandco.familyfinance.network.model.UserSettingsApiModel;
import com.kasandco.familyfinance.utils.IsNetworkConnect;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import java.util.HashMap;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class SettingsRepository extends BaseRepository {
    UserNetworkInterface userNetworkInterface;

    @Inject
    public SettingsRepository(SharedPreferenceUtil sharedPreferenceUtil, IsNetworkConnect isNetworkConnect, Retrofit retrofit) {
        super(sharedPreferenceUtil, isNetworkConnect);
        userNetworkInterface = retrofit.create(UserNetworkInterface.class);
    }

    public void saveSettingsToServer(){
        if(isLogged && isNetworkConnect.isInternetAvailable()) {
            Requests.RequestsInterface<ResponseBody> callbackResponse = new Requests.RequestsInterface<ResponseBody>() {
                @Override
                public void success(ResponseBody responseObj) {

                }

                @Override
                public void error() {

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
