package com.kasandco.shoplist.app.item;

import androidx.room.Dao;
import androidx.room.Query;

import com.kasandco.shoplist.dao.BaseDao;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;


@Dao
public interface ItemDao extends BaseDao<ItemModel> {
    @Query("SELECT * FROM list_item WHERE local_list_id=:listId AND is_delete=0 ORDER BY status DESC, date_mod DESC")
    Flowable<List<ItemModel>> getAllItems(long listId);

    @Query("SELECT * FROM list_item WHERE local_list_id=:listId ORDER BY status DESC, date_mod DESC")
    List<ItemModel> getItems(long listId);

    @Query("SELECT * FROM list_item WHERE local_list_id=:listId AND status=1")
    List<ItemModel> getActiveItems(long listId);

    @Query("UPDATE list_item SET server_image_name='', image_path=:path WHERE id=:id")
    void saveImagePath(String path, long id);

    @Query("DELETE FROM list_item WHERE local_list_id = :localListId AND status=1")
    void deleteActiveItems(long localListId);

    @Query("DELETE FROM list_item WHERE local_list_id = :localListId AND status=0")
    void deleteInActiveItems(long localListId);

    @Query("UPDATE list_item SET is_delete=1 WHERE local_list_id = :localListId AND status=1")
    void softDeleteActiveItems(long localListId);

    @Query("UPDATE list_item SET is_delete=1 WHERE local_list_id = :localListId AND status=0")
    void softDeleteInActiveItems(long localListId);

    @Query("SELECT * FROM list_item WHERE id = :id")
    ItemModel getItem(long id);

    @Query("UPDATE list_item SET server_image_name = :imageName WHERE id = :id")
    void setServerImageName(long id, String imageName);

    @Query("SELECT * FROM list_item WHERE server_id=:serverId")
    ItemModel getItemForServerId(long serverId);

}
