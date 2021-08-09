package com.kasandco.familyfinance.network.service;

import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import javax.inject.Inject;

public abstract class BaseNetworkService implements Constants {
    @Inject
    SharedPreferenceUtil sharedPreferenceUtil;
    protected String getApiKey(){
        return sharedPreferenceUtil.getSharedPreferences().getString(API_KEY, null);
    }
}
