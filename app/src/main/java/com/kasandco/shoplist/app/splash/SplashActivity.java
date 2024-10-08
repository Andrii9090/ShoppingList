package com.kasandco.shoplist.app.splash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.kasandco.shoplist.App;
import com.kasandco.shoplist.R;
import com.kasandco.shoplist.app.settings.SettingsActivity;
import com.kasandco.shoplist.core.icon.IconDao;
import com.kasandco.shoplist.core.icon.IconModel;
import com.kasandco.shoplist.app.list.ListActivity;
import com.kasandco.shoplist.core.Constants;
import com.kasandco.shoplist.utils.SharedPreferenceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity implements Constants {
    @Inject
    IconDao iconDao;
    @Inject
    SharedPreferenceUtil sharedPreferenceUtil;
    ImageView logo;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.appComponent.plus(new SplashModule()).inject(this);
        super.onCreate(savedInstanceState);
        int themeResource = sharedPreferenceUtil.getSharedPreferences().getInt(Constants.COLOR_THEME, Constants.THEME_DEFAULT);
        setTheme(themeResource);
        setContentView(R.layout.activity_splash);
        logo = findViewById(R.id.splash_logo);

        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        sharedPreferenceUtil.getEditor().putString(Constants.DEVICE_ID, deviceId).apply();

        new Thread(() -> {
            if (sharedPreferenceUtil.getSharedPreferences().getInt(IS_ADDED_ICONS, 0) == 0) {
                List<String> iconsPath = listAssetFiles(SplashActivity.this, "icons");
                for (String path : iconsPath) {
                    iconDao.insert(new IconModel(path, null));
                }
                if (iconsPath.size() > 0) {
                    sharedPreferenceUtil.getEditor().putInt(IS_ADDED_ICONS, 1).apply();
                }
            }
        }).start();
        showAdd();
    }

    private void showAdd() {
        if(!sharedPreferenceUtil.isPro()) {
            MobileAds.initialize(this, initializationStatus -> {});
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(this,"ca-app-pub-2199413045845818/9300584376", adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            mInterstitialAd = interstitialAd;
                            mInterstitialAd.show(SplashActivity.this);
                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                                @Override
                                public void onAdClicked() {
                                    startMainActivity();
                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    mInterstitialAd = null;
                                    startMainActivity();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    mInterstitialAd = null;
                                    startMainActivity();
                                }

                                @Override
                                public void onAdImpression() {
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                }
                            });

                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            mInterstitialAd = null;
                            startMainActivity();
                        }
                    });
        }else{
            startMainActivity();
        }
    }

    private void startMainActivity() {
        Intent intent;
        if (sharedPreferenceUtil.getSharedPreferences().getBoolean(Constants.IS_FIRST_START, false)) {
            intent = new Intent(this, ListActivity.class);
        } else {
            intent = new Intent(this, SettingsActivity.class);
            sharedPreferenceUtil.getEditor().putBoolean(Constants.IS_FIRST_START, true).apply();
        }
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1f)
                .setDuration(1000);

        valueAnimator.addUpdateListener(valueAnimator1 -> logo.setAlpha((float) valueAnimator1.getAnimatedValue()));
        valueAnimator.start();

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                try {
                    int timeStart = 600;
                    if (sharedPreferenceUtil.getSharedPreferences().getInt(Constants.IS_ADDED_ICONS, 0) == 0) {
                        timeStart = 2000;
                    }
                    Thread.sleep(timeStart);
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