package com.kasandco.familyfinance.app.splash;

import com.kasandco.familyfinance.core.icon.IconDao;
import com.kasandco.familyfinance.core.AppDataBase;

import dagger.Module;
import dagger.Provides;

@Module
public class SplashModule {
    @Provides
    IconDao providesIconDao(AppDataBase appDataBase){
        return appDataBase.getIconDao();
    }
}
