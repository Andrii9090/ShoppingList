package com.kasandco.familyfinance.core;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.kasandco.familyfinance.app.icon.IconDao;
import com.kasandco.familyfinance.app.icon.IconModel;
import com.kasandco.familyfinance.app.list.ListActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RoomModule{
    @Provides
    @Singleton
    AppDataBase createRoom(@Named("application.context") Context appContext) {
        AppDataBase db = Room.databaseBuilder(appContext, AppDataBase.class, "cost_history.db")
                .build();
        return db;
    }
    @Provides
    @Singleton
    IconDao provideIconDao(AppDataBase appDataBase){
        return appDataBase.getIconDao();
    }
}