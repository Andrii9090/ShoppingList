package com.kasandco.shoplist.core;

import android.content.Context;

import androidx.room.Room;

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
}