package com.kasandco.familyfinance.app.list;

import androidx.room.Dao;
import androidx.room.Query;

import com.kasandco.familyfinance.dao.BaseDao;

import java.util.List;

@Dao
public interface ListSyncHistoryDao extends BaseDao<ListSyncHistoryModel> {
    @Query("SELECT * FROM list_sync_history")
    List<ListSyncHistoryModel> getAll();

    @Query("DELETE FROM list_sync_history")
    void clear();
}
