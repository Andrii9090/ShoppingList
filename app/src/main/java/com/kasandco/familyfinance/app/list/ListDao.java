package com.kasandco.familyfinance.app.list;

import androidx.room.Dao;
import androidx.room.Query;

import com.kasandco.familyfinance.dao.BaseDao;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;


@Dao
public interface ListDao extends BaseDao<ListModel> {
    @Query("SELECT * FROM list WHERE id=:id")
    ListModel getList(long id);

    @Query("SELECT * FROM list")
    List<ListModel> getAllList();

    @Query("SELECT * FROM list WHERE is_delete=0 ORDER BY date_mod DESC")
    Flowable<List<ListModel>> getAllActiveList();

    @Query("SELECT * FROM list WHERE is_delete=1")
    List<ListModel> getDeletedLists();

    @Query("SELECT * FROM list WHERE cost_category_id=:costCategoryId")
    List<ListModel> getListsForCostId(long costCategoryId);

    @Query("SELECT id FROM list ORDER BY id DESC LIMIT 1")
    Single<Long> getLastId();

    @Query("UPDATE list SET quantity_inactive=0 WHERE id=:id")
    void clearInactiveItems(long id);

    @Query("UPDATE list SET quantity_active=0 WHERE id=:id")
    void clearActiveItems(long id);

    @Query("UPDATE list SET cost_category_id=:categoryId WHERE id=:id")
    void addFinanceCategoryId(long id, long categoryId);

    @Query("UPDATE list SET date_mod=:dateMod, server_id=:serverId WHERE id=:id")
    void updateServerId(long id, Long serverId, String dateMod);

    @Query("SELECT id FROM list WHERE server_id =:serverId")
    long getId(Long serverId);

    @Query("SELECT server_id FROM finance_category WHERE id =:financeCategoryId")
    long getFinanceCategoryId(long financeCategoryId);

    @Query("DELETE FROM list WHERE id=:id OR server_id=:serverId")
    void delete(long id, long serverId);

    @Query("DELETE FROM list_item WHERE local_list_id=:localListId OR server_list_id=:serverListId")
    void deleteListItems(long localListId, long serverListId);

    @Query("SELECT server_id FROM list WHERE id=:listId")
    long getServerListId(long listId);
}
