package com.kasandco.shoplist.core;

import android.content.Context;

import com.kasandco.shoplist.utils.SharedPreferenceUtil;

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
}
