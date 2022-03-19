package com.kasandco.shoplist.app.list;

import androidx.room.Dao;
import androidx.room.Query;

import com.kasandco.shoplist.dao.BaseDao;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;


@Dao
public interface ListDao extends BaseDao<ListModel> {
    @Query("SELECT * FROM list")
    List<ListModel> getAllList();

    @Query("SELECT * FROM list WHERE is_delete=0 ORDER BY date_mod DESC")
    Flowable<List<ListModel>> getAllActiveList();

    @Query("SELECT id FROM list WHERE server_id =:serverId")
    long getId(Long serverId);

    @Query("DELETE FROM list WHERE id=:id OR server_id=:serverId")
    void delete(long id, long serverId);

    @Query("DELETE FROM list_item WHERE local_list_id=:localListId OR server_list_id=:serverListId")
    void deleteListItems(long localListId, long serverListId);

    @Query("SELECT server_id FROM list WHERE id=:listId")
    long getServerListId(long listId);

    @Query("SELECT * FROM list WHERE server_id=:serverId")
    ListModel getListForServerId(long serverId);

    @Query("SELECT COUNT(*) FROM list WHERE is_delete=0")
    int getQuantity();
}
