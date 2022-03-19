package com.kasandco.shoplist.app.item;

import com.kasandco.shoplist.core.AppDataBase;

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
