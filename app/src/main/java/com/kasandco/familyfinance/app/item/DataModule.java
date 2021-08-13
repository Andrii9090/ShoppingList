package com.kasandco.familyfinance.app.item;

import com.kasandco.familyfinance.app.ViewModelCreateFactory;
import com.kasandco.familyfinance.core.AppDataBase;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    @ItemActivityScope
    @Provides
    ViewModelItem providesViewModel(ViewModelCreateFactory viewModelCreateFactory) {
        return viewModelCreateFactory.create(ViewModelItem.class);
    }

    @ItemActivityScope
    @Provides
    ItemDao providesDao(AppDataBase appDataBase) {
        return appDataBase.getItemDao();
    }
}
