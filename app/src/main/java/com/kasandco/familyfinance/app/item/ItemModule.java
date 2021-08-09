package com.kasandco.familyfinance.app.item;

import android.content.Context;

import com.kasandco.familyfinance.core.AppDataBase;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ItemModule {
    @ItemActivityScope
    @Named("context")
    Context context;

    public ItemModule(@Named("context") Context context){
        this.context = context;
    }

    @Provides
    @ItemActivityScope
    @Named("context") Context provideContext(){
        return context;
    }

    @Provides
    @ItemActivityScope
    ItemDao provideDao(AppDataBase appDataBase){
        return appDataBase.getItemDao();
    }
}
