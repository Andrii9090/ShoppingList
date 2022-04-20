package com.kasandco.shoplist.utils;

import android.app.Activity;
import android.view.View;

import com.kasandco.shoplist.core.Constants;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class ShowCaseUtil {
    private GuideView.Builder builder;
    private GuideView showcaseView;
    private Activity activity;
    private SharedPreferenceUtil sharedPreferenceUtil;

    public ShowCaseUtil(Activity _act, SharedPreferenceUtil _sharedPreferenceUtil) {
        activity = _act;
        sharedPreferenceUtil = _sharedPreferenceUtil;
    }

    public void setCase(View viewId, int resIdTitle, int resIdText) {
        createShowCaseObl(activity);
        setShowCase(viewId, resIdTitle, resIdText);

    }

    public void setOnClickListener(GuideListener listener) {
        builder.setGuideListener(listener);
    }

    private void createShowCaseObl(Activity activity) {
        builder = new GuideView.Builder(activity);
    }

    private void setShowCase(View viewTarget, int resIdTitle, int resIdText) {
        builder.setTargetView(viewTarget)
                .setDismissType(DismissType.outside)
                .setTitle(activity.getString(resIdTitle))
                .setContentText(activity.getString(resIdText));
    }

    public void show() {
        showcaseView = builder.build();
        showcaseView.show();
        if(showcaseView.isShowing()){
            sharedPreferenceUtil.getEditor().putString(Constants.IS_SHOW_INFO_ADD_LIST, "").apply();
        }

    }

    public void hide() {
        showcaseView.dismiss();
    }
}
