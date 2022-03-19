package com.kasandco.shoplist.app.splash;

import com.kasandco.shoplist.core.icon.IconDao;
import com.kasandco.shoplist.core.AppDataBase;

import dagger.Module;
import dagger.Provides;

@Module
public class SplashModule {
    @Provides
    IconDao providesIconDao(AppDataBase appDataBase){
        return appDataBase.getIconDao();
    }
}
