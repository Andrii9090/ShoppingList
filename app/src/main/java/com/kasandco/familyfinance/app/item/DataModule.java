package com.kasandco.familyfinance.app.item;

import android.content.Context;

import com.kasandco.familyfinance.app.ViewModelCreateFactory;
import com.kasandco.familyfinance.core.AppDataBase;
import com.kasandco.familyfinance.utils.SaveImageUtils;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    @ItemActivityScope
    @Provides
    ItemDao providesDao(AppDataBase appDataBase) {
        return appDataBase.getItemDao();
    }
}
