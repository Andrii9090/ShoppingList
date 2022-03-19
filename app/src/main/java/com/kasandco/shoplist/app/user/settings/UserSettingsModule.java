package com.kasandco.shoplist.app.user.settings;

import android.app.Activity;

import com.kasandco.shoplist.core.AppDataBase;
import com.kasandco.shoplist.network.UserNetworkInterface;
import com.kasandco.shoplist.utils.NetworkConnect;
import com.kasandco.shoplist.utils.SharedPreferenceUtil;
import com.kasandco.shoplist.utils.ShowCaseUtil;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class UserSettingsModule {
    private Activity activity;

    public UserSettingsModule(Activity activity){
        this.activity = activity;
    }

    @Provides
    @UserSettingScope
    Activity providesActivity(){
        return activity;
    }

    @Provides
    @UserSettingScope
    UserSettingsRepository providesUserSettingsRepository(SharedPreferenceUtil sharedPreferenceUtil, AppDataBase appDataBase, Retrofit retrofit, NetworkConnect networkConnect) {
        return new UserSettingsRepository(sharedPreferenceUtil, appDataBase, retrofit.create(UserNetworkInterface.class), networkConnect);
    }

    @Provides
    @UserSettingScope
    ShowCaseUtil providesShowCaseUtil(Activity activity){
        return new ShowCaseUtil(activity);
    }
}
