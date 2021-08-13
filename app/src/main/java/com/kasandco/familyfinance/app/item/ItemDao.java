package com.kasandco.familyfinance.app.item;

import androidx.room.Dao;
import androidx.room.Query;

import com.kasandco.familyfinance.dao.BaseDao;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.rxjava3.core.Flowable;


@Dao
public interface ItemDao extends BaseDao<ItemModel> {
    @Query("SELECT * FROM list_item WHERE status=1 ORDER BY date_mod DESC")
    Flowable<List<ItemModel>> getAllActiveItems();

    @Query("SELECT * FROM list_item WHERE status=0")
    List<ItemModel> getAllNotActive();

    @Query("SELECT * FROM list_item WHERE id=:id")
    ItemModel getListItem(long id);

    @Query("SELECT * FROM list_item WHERE id=(SELECT MAX(id) FROM list_item)")
    Flowable<ItemModel> getLastId();
}
