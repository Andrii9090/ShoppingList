package com.kasandco.familyfinance.app.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.core.icon.IconDao;
import com.kasandco.familyfinance.core.icon.IconModel;
import com.kasandco.familyfinance.app.list.ListActivity;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity implements Constants {
    @Inject
    IconDao iconDao;
    SharedPreferenceUtil sharedPreferenceUtil;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferenceUtil = new SharedPreferenceUtil(this);
        int themeResource = sharedPreferenceUtil.getSharedPreferences().getInt(Constants.COLOR_THEME, R.style.Theme_FamilyFinance);
        setTheme(themeResource);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        App.appComponent.plus(new SplashModule()).inject(this);
        logo = findViewById(R.id.splash_logo);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (sharedPreferenceUtil.getSharedPreferences().getInt(IS_ADDED_ICONS, 0) == 0) {
                    List<String> iconsPath = listAssetFiles(SplashActivity.this, "icons");
                    for (String path : iconsPath) {
                        iconDao.insert(new IconModel(path, null));
                    }
                    if (iconsPath.size() > 0) {
                        sharedPreferenceUtil.getEditor().putInt(IS_ADDED_ICONS, 1).apply();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, ListActivity.class);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1f)
                .setDuration(1000);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                logo.setAlpha((float) valueAnimator.getAnimatedValue());
            }
        });
        valueAnimator.start();

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                try {
                    Thread.sleep(500);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    List<String> listAssetFiles(Context appContext, String path) {
        String[] list;
        List<String> pathList = new ArrayList<>();
        try {
            list = appContext.getAssets().list(path);
            if (list.length > 0) {
                for (String file : list) {
                    if (listAssetFiles(appContext, path + "/" + file) == null)
                        return null;
                    else {
                        pathList.add(path + "/" + file);
                    }
                }
            }
        } catch (IOException e) {
            return null;
        }
        return pathList;
    }

}