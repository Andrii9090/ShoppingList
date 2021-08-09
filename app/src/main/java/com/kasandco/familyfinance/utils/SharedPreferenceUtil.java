package com.kasandco.familyfinance.utils;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

public class SharedPreferenceUtil {
    private static final String SHARED_PREFERENCE_NAME = "family_cost";
    private final SharedPreferences sharedPreferences;

    @Inject
    public SharedPreferenceUtil(Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public SharedPreferences.Editor getEditor(){
        return sharedPreferences.edit();
    }

    public SharedPreferences getSharedPreferences(){
        return sharedPreferences;
    }
}
