package com.kasandco.familyfinance.utils;

import static com.kasandco.familyfinance.core.Constants.TOKEN;

import android.content.Context;
import android.content.SharedPreferences;

import com.kasandco.familyfinance.core.Constants;

import javax.inject.Inject;

public class SharedPreferenceUtil {
    private static final String SHARED_PREFERENCE_NAME = "family_cost";
    private final SharedPreferences sharedPreferences;

    public SharedPreferenceUtil(Context context){
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

    public String getDeviceId() {
        return getSharedPreferences().getString(Constants.DEVICE_ID,null);
    }
}
