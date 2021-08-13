package com.kasandco.familyfinance.core;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.kasandco.familyfinance.app.ViewModelCreateFactory;
import com.kasandco.familyfinance.app.icon.IconDao;
import com.kasandco.familyfinance.app.item.ItemDao;
import com.kasandco.familyfinance.app.item.ViewModelItem;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {NetworkModule.class, RoomModule.class})
public class AppModule {
    @Named("application.context")
    Context context;

    public AppModule(@Named("application.context") Context context){
        this.context = context;
    }

    @Provides
    @Singleton
    @Named("application.context")
    Context provideContext(){
        return context;
    }

    @Provides
    @Singleton
    SharedPreferenceUtil provideSharedPreference(@Named("application.context") Context context){
        return new SharedPreferenceUtil(context);
    }

    @Provides
    @Singleton
    ViewModelCreateFactory providesViewModelFactory(){
        return new ViewModelCreateFactory();
    }
}
