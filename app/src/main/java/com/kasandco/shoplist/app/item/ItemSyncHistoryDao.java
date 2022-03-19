package com.kasandco.shoplist.app.item;

import androidx.room.Dao;
import androidx.room.Query;

import com.kasandco.shoplist.dao.BaseDao;

import java.util.List;

@Dao
public interface ItemSyncHistoryDao extends BaseDao<ItemSyncHistoryModel> {
    @Query("SELECT * FROM item_sync_history WHERE server_list_id =:serverListId")
    List<ItemSyncHistoryModel> getAll(long serverListId);

    @Query("DELETE FROM item_sync_history WHERE server_list_id = :serverListId")
    void clearAll(long serverListId);


}
