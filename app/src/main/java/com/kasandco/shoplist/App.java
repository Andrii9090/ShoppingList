package com.kasandco.shoplist;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.kasandco.shoplist.app.item.ItemComponent;
import com.kasandco.shoplist.app.item.ItemModule;
import com.kasandco.shoplist.app.list.ListActivityComponent;
import com.kasandco.shoplist.app.list.ListModule;
import com.kasandco.shoplist.core.AppComponent;
import com.kasandco.shoplist.core.AppModule;
import com.kasandco.shoplist.core.DaggerAppComponent;


public class App extends Application {

    public static AppComponent appComponent;
    public static ListActivityComponent listActivityComponent;
    public static ItemComponent itemComponent;

    @Override
    public void onCreate() {
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(getApplicationContext())).build();
        super.onCreate();
        startAddMob();
    }

    public static void recreateDagger(Context appContext){
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(appContext)).build();
    }

    public static AppComponent getAppComponent(){
        return appComponent;
    }

    public static ListActivityComponent getListActivityComponent(Context context){
        if(listActivityComponent==null){
            return getAppComponent().plus(new ListModule(context));
        }
        return listActivityComponent;
    }

    public static ItemComponent getItemComponent(Context context){
        if(itemComponent==null){
            return getAppComponent().plus(new ItemModule(context));
        }
        return itemComponent;
    }

    private void startAddMob() {
        MobileAds.initialize(this, initializationStatus -> Log.e("Status ADDMOB", initializationStatus.toString()));
    }
}
