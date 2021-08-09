package com.kasandco.familyfinance.app.item;

import androidx.room.Dao;
import androidx.room.Query;

import com.kasandco.familyfinance.dao.BaseDao;

import java.util.List;

@Dao
public interface ItemDao extends BaseDao<ItemModel> {
    @Query("SELECT * FROM list_item WHERE status=1")
    List<ItemModel> getAllItems();

    @Query("SELECT * FROM list_item WHERE status=0")
    List<ItemModel> getAllNotActive();

    @Query("SELECT * FROM list_item WHERE id=:id")
    ItemModel getListItem(long id);
}
