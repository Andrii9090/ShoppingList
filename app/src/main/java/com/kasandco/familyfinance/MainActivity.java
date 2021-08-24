package com.kasandco.familyfinance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.kasandco.familyfinance.app.expenseHistory.FinanceActivity;
import com.kasandco.familyfinance.app.icon.IconDao;
import com.kasandco.familyfinance.app.icon.IconModel;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements Constants {
    @Inject
    IconDao iconDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, FinanceActivity.class);
        startActivity(intent);
        SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(this);
        App.appComponent.inject(this);
        if(sharedPreferenceUtil.getSharedPreferences().getInt(IS_ADDED_ICONS, 0)==0){
            List<String> iconsPath = listAssetFiles(this, "icons");
            for (String path : iconsPath) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        iconDao.insert(new IconModel(path, null));
                    }
                });
            }
            if (iconsPath.size() > 0) {
                sharedPreferenceUtil.getEditor().putInt(IS_ADDED_ICONS, 1).apply();
            }
        }
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