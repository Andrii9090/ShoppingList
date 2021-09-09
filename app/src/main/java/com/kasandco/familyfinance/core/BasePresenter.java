package com.kasandco.familyfinance.core;

import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import javax.inject.Inject;

public abstract class BasePresenter<V> implements Constants {
    protected V view;
    @Inject
    SharedPreferenceUtil preferenceUtil;

    protected boolean isUser(){
        return preferenceUtil.getSharedPreferences().getString(API_KEY, null) != null;
    }

    public abstract void viewReady(V view);

    public void destroy(){
        view = null;
    };
}
