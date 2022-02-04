package com.kasandco.familyfinance.utils;

import android.app.Activity;
import android.view.View;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.kasandco.familyfinance.R;

public class ShowCaseUtil {
    private ShowcaseView.Builder builder;
    private ShowcaseView showcaseView;
    private Activity activity;
    private int lastVieId;

    public ShowCaseUtil(Activity _act){
        activity = _act;
    }

    public void setCase(int viewId, int resIdTitle, int resIdText) {
        lastVieId = viewId;
        ViewTarget viewTarget = new ViewTarget(viewId, activity);
        if (builder == null) {
            createShowCaseObl(activity);
        }
        setShowCase(viewTarget, resIdTitle, resIdText);
    }

    public void setOnClickListener(View.OnClickListener listener){
        builder.setOnClickListener(listener);
    }

    private void createShowCaseObl(Activity activity) {
        builder = new ShowcaseView.Builder(activity);
    }

    private void setShowCase(ViewTarget viewTarget, int resIdTitle, int resIdText) {
        builder.setTarget(viewTarget)
                .setStyle(R.style.Theme_FamilyFinance)
                .setContentTitle(resIdTitle)
                .blockAllTouches()
                .hideOnTouchOutside()
                .setContentText(resIdText);
    }

    public void show() {
        if(showcaseView==null) {
            showcaseView = builder.build();
        }
        showcaseView.show();
    }

    public int getLastVieId() {
        return lastVieId;
    }

    public void hide(){
        showcaseView.hide();
    }
}
