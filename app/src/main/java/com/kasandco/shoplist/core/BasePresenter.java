package com.kasandco.shoplist.core;

import com.kasandco.shoplist.utils.SharedPreferenceUtil;

import javax.inject.Inject;

public abstract class BasePresenter<V> implements Constants {
    protected V view;
    @Inject
    SharedPreferenceUtil preferenceUtil;

    public abstract void viewReady(V view);

    public void destroy(){
        view = null;
    }

    abstract public void swipeRefresh();
}
