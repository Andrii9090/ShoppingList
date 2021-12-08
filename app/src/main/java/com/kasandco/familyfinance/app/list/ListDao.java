package com.kasandco.familyfinance.app.list;

import androidx.room.Dao;
import androidx.room.Query;

import com.kasandco.familyfinance.dao.BaseDao;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;


@Dao
public interface ListDao extends BaseDao<ListModel> {
    @Query("SELECT * FROM list")
    List<ListModel> getAllList();

    @Query("SELECT *, (SELECT COUNT(*) FROM list_item WHERE local_list_id=list.id AND status=1) as quantity_active, (SELECT COUNT(*) FROM list_item WHERE local_list_id=list.id AND status=0) as quantity_inactive FROM list WHERE is_delete=0 ORDER BY date_mod DESC")
    Flowable<List<ListModel>> getAllActiveList();

    @Query("UPDATE list SET quantity_inactive=0 WHERE id=:id")
    void clearInactiveItems(long id);

    @Query("UPDATE list SET quantity_active=0 WHERE id=:id")
    void clearActiveItems(long id);

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
}
