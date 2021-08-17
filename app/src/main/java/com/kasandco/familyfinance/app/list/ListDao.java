package com.kasandco.familyfinance.app.list;

import androidx.room.Dao;
import androidx.room.Query;

import com.kasandco.familyfinance.dao.BaseDao;

import java.util.List;

import io.reactivex.Flowable;

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

    @Query("SELECT MAX(id) FROM list")
    long getLastId();

    @Query("UPDATE list SET quantity_inactive=0 WHERE id=:id")
    void clearInactiveItems(long id);

    @Query("UPDATE list SET quantity_active=0 WHERE id=:id")
    void clearActiveItems(long id);

    @Query("DELETE FROM list_item WHERE local_list_id=:localListId AND status=1")
    void deleteActiveListItem(long localListId);

    @Query("DELETE FROM list_item WHERE local_list_id=:localListId AND status=0")
    void deleteInactiveListItem(long localListId);
}
