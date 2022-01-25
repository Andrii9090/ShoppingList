package com.kasandco.familyfinance.app.user.settings;

import com.kasandco.familyfinance.core.AppDataBase;
import com.kasandco.familyfinance.network.UserNetworkInterface;
import com.kasandco.familyfinance.utils.NetworkConnect;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class UserSettingsModule {
    @Provides
    UserSettingsRepository providesUserSettingsRepository(SharedPreferenceUtil sharedPreferenceUtil, AppDataBase appDataBase, Retrofit retrofit, NetworkConnect networkConnect) {
        return new UserSettingsRepository(sharedPreferenceUtil, appDataBase, retrofit.create(UserNetworkInterface.class), networkConnect);
    }
}
