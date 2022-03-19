package com.kasandco.shoplist.app.settings;


import com.kasandco.shoplist.utils.NetworkConnect;
import com.kasandco.shoplist.utils.SharedPreferenceUtil;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class SettingsModule {

    @Provides
    @SettingsScope
    SettingsRepository providesRepository(SharedPreferenceUtil sharedPreferenceUtil, NetworkConnect isNetwork, Retrofit retrofit){
        return new SettingsRepository(sharedPreferenceUtil,isNetwork, retrofit);
    }
}
