package com.kasandco.familyfinance.core;

import com.kasandco.familyfinance.utils.IsNetworkConnect;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

abstract public class BaseRepository {
    protected SharedPreferenceUtil sharedPreference;
    protected IsNetworkConnect isNetworkConnect;
    protected boolean isLogged;
    protected String deviceId;


    protected BaseRepository(SharedPreferenceUtil sharedPreferenceUtil, IsNetworkConnect isNetworkConnect){
        this.sharedPreference = sharedPreferenceUtil;
        this.isNetworkConnect = isNetworkConnect;
        this.isLogged = sharedPreferenceUtil.isLogged();
        this.deviceId = sharedPreferenceUtil.getDeviceId();
    }
}
