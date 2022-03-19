package com.kasandco.shoplist.core;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.kasandco.shoplist.app.item.ItemSyncHistoryDao;
import com.kasandco.shoplist.app.item.ItemSyncHistoryModel;
import com.kasandco.shoplist.app.list.ListSyncHistoryModel;
import com.kasandco.shoplist.app.list.ListSyncHistoryDao;
import com.kasandco.shoplist.core.icon.IconDao;
import com.kasandco.shoplist.core.icon.IconModel;
import com.kasandco.shoplist.app.item.ItemDao;
import com.kasandco.shoplist.app.item.ItemModel;
import com.kasandco.shoplist.app.list.ListModel;
import com.kasandco.shoplist.app.list.ListDao;

@Database(entities = {ListModel.class, ItemSyncHistoryModel.class, ListSyncHistoryModel.class, IconModel.class, ItemModel.class}, exportSchema = false, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract ListDao getListDao();
    public abstract IconDao getIconDao();
    public abstract ItemDao getItemDao();
    public abstract ListSyncHistoryDao getListSyncDao();
    public abstract ItemSyncHistoryDao getItemSyncDao();
}
