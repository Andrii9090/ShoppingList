package com.kasandco.familyfinance.core;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.kasandco.familyfinance.app.icon.IconDao;
import com.kasandco.familyfinance.app.icon.IconModel;
import com.kasandco.familyfinance.app.item.ItemDao;
import com.kasandco.familyfinance.app.item.ItemModel;
import com.kasandco.familyfinance.app.list.ListModel;
import com.kasandco.familyfinance.app.list.ListDao;

@Database(entities = {ListModel.class, IconModel.class, ItemModel.class}, exportSchema = false, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract ListDao getListDao();
    public abstract IconDao getIconDao();
    public abstract ItemDao getItemDao();
}
