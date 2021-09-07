package com.kasandco.familyfinance.app.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.icon.IconDao;
import com.kasandco.familyfinance.app.icon.IconModel;
import com.kasandco.familyfinance.app.list.ListActivity;
import com.kasandco.familyfinance.app.statistic.StatisticActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Intent intent = new Intent(this, ListActivity.class);
        SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(this);
        App.appComponent.plus(new SplashModule()).inject(this);
        Handler handler =new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(sharedPreferenceUtil.getSharedPreferences().getInt(IS_ADDED_ICONS, 0)==0){
                    List<String> iconsPath = listAssetFiles(SplashActivity.this, "icons");
                    for (String path : iconsPath) {
                                iconDao.insert(new IconModel(path, null));
                    }
                    if (iconsPath.size() > 0) {
                        sharedPreferenceUtil.getEditor().putInt(IS_ADDED_ICONS, 1).apply();
                    }
                }
                try {
                    Thread.sleep(1200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                });
            }
        }).start();
    }

    List<String> listAssetFiles(Context appContext, String path) {
        String [] list;
        List<String> pathList =  new ArrayList<>();
        try {
            list = appContext.getAssets().list(path);
            if (list.length > 0) {
                for (String file : list) {
                    if (listAssetFiles(appContext, path + "/" + file)==null)
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