package com.kasandco.familyfinance.app.settings;


import com.kasandco.familyfinance.utils.IsNetworkConnect;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class SettingsModule {

    @Provides
    @SettingsScope
    SettingsRepository providesRepository(SharedPreferenceUtil sharedPreferenceUtil, IsNetworkConnect isNetwork, Retrofit retrofit){
        return new SettingsRepository(sharedPreferenceUtil,isNetwork, retrofit);
    }
}
