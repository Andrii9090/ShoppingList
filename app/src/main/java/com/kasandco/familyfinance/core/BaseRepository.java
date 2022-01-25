package com.kasandco.familyfinance.core;

import com.kasandco.familyfinance.utils.NetworkConnect;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

abstract public class BaseRepository {
    protected SharedPreferenceUtil sharedPreference;
    protected NetworkConnect isNetworkConnect;
    protected boolean isLogged;
    protected String deviceId;


    protected BaseRepository(SharedPreferenceUtil sharedPreferenceUtil, NetworkConnect isNetworkConnect){
        this.sharedPreference = sharedPreferenceUtil;
        this.isNetworkConnect = isNetworkConnect;
        this.isLogged = sharedPreferenceUtil.isLogged();
        this.deviceId = sharedPreferenceUtil.getDeviceId();
    }
}
