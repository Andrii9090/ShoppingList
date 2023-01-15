package com.kasandco.shoplist.utils;

import static com.kasandco.shoplist.core.Constants.IS_PRO;
import static com.kasandco.shoplist.core.Constants.TOKEN;

import android.content.Context;
import android.content.SharedPreferences;

import com.kasandco.shoplist.core.Constants;


public class SharedPreferenceUtil {
    private static final String SHARED_PREFERENCE_NAME = "family_cost";
    private final SharedPreferences sharedPreferences;
    private Context context;

    public SharedPreferenceUtil(Context context){
        this.context =context;
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public SharedPreferences.Editor getEditor(){
        return sharedPreferences.edit();
    }

    public SharedPreferences getSharedPreferences(){
        return sharedPreferences;
    }

    public boolean isLogged(){
        return getSharedPreferences().getString(TOKEN, null) != null;
    }

    public boolean isPro(){
        return getSharedPreferences().getBoolean(IS_PRO, false);
    }

    public String getDeviceId() {
        return getSharedPreferences().getString(Constants.DEVICE_ID,null);
    }

    public void logout() {
        getEditor().putString(Constants.EMAIL, null).commit();
        getEditor().putString(Constants.TOKEN, null).commit();
        getEditor().putString(Constants.UUID, null).commit();
        getEditor().putString(Constants.FMC_TOKEN, null).commit();
        getEditor().putString(Constants.USER_NAME, null).commit();
        getEditor().putString(Constants.IS_PRO, null).commit();
        getEditor().putString(Constants.SUBSCR_TOKEN, null).commit();
    }

    public void setIsPro(boolean isPro) {
        getEditor().putBoolean(IS_PRO, isPro).apply();
    }
}
