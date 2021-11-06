package com.kasandco.familyfinance.core;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.kasandco.familyfinance.app.finance.models.FinanceCategoryDao;
import com.kasandco.familyfinance.app.finance.models.FinanceCategoryModel;
import com.kasandco.familyfinance.app.finance.models.FinanceCategorySync;
import com.kasandco.familyfinance.app.finance.models.FinanceCategorySyncDao;
import com.kasandco.familyfinance.app.finance.models.FinanceDao;
import com.kasandco.familyfinance.app.finance.models.FinanceHistoryModel;
import com.kasandco.familyfinance.app.finance.models.FinanceHistorySync;
import com.kasandco.familyfinance.app.finance.models.FinanceHistorySyncDao;
import com.kasandco.familyfinance.app.item.ItemSyncHistoryDao;
import com.kasandco.familyfinance.app.item.ItemSyncHistoryModel;
import com.kasandco.familyfinance.app.list.ListSyncHistoryModel;
import com.kasandco.familyfinance.app.list.ListSyncHistoryDao;
import com.kasandco.familyfinance.core.icon.IconDao;
import com.kasandco.familyfinance.core.icon.IconModel;
import com.kasandco.familyfinance.app.item.ItemDao;
import com.kasandco.familyfinance.app.item.ItemModel;
import com.kasandco.familyfinance.app.list.ListModel;
import com.kasandco.familyfinance.app.list.ListDao;

@Database(entities = {ListModel.class, FinanceHistorySync.class, FinanceCategorySync.class, ItemSyncHistoryModel.class, ListSyncHistoryModel.class, IconModel.class, ItemModel.class, FinanceCategoryModel.class, FinanceHistoryModel.class}, exportSchema = false, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract ListDao getListDao();
    public abstract IconDao getIconDao();
    public abstract ItemDao getItemDao();
    public abstract FinanceCategoryDao getFinanceCategoryDao();
    public abstract FinanceDao getFinanceDao();
    public abstract ListSyncHistoryDao getListSyncDao();
    public abstract ItemSyncHistoryDao getItemSyncDao();
    public abstract FinanceCategorySyncDao getFinanceCategorySyncDao();
    public abstract FinanceHistorySyncDao getFinanceHistorySyncDao();
}
