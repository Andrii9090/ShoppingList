package com.kasandco.familyfinance.app.item;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Update;

import com.kasandco.familyfinance.dao.BaseDao;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;


@Dao
public interface ItemDao extends BaseDao<ItemModel> {
    @Query("SELECT * FROM list_item WHERE local_list_id=:listId ORDER BY status DESC, date_mod ASC")
    Flowable<List<ItemModel>> getAllActiveItems(long listId);

    @Query("SELECT * FROM list_item WHERE status=0")
    List<ItemModel> getAllNotActive();

    @Query("SELECT * FROM list_item WHERE id=:id")
    ItemModel getListItem(long id);

    @Query("SELECT * FROM list_item WHERE id=(SELECT MAX(id) FROM list_item)")
    Flowable<ItemModel> getLastId();

    @Query("UPDATE list_item SET  image_path=:path WHERE id=:id")
    void saveImagePath(String path, long id);

    @Query("UPDATE list SET quantity_active=quantity_active+1 WHERE id=:listId")
    void plusActiveItemsInList(long listId);

    @Query("UPDATE list SET quantity_active=quantity_active-1 WHERE id=:listId")
    void minusActiveItemsInList(long listId);

    @Query("UPDATE list SET quantity_inactive=quantity_inactive+1 WHERE id=:listId")
    void plusInactiveItemsInList(long listId);

    @Query("UPDATE list SET quantity_inactive=quantity_inactive-1 WHERE id=:listId")
    void minusInactiveItemsInList(long listId);
}
