package com.kasandco.familyfinance.app.list;

import androidx.room.Dao;
import androidx.room.Query;

import com.kasandco.familyfinance.dao.BaseDao;

import java.util.List;
@Dao
public interface ListDao extends BaseDao<ListModel> {
    @Query("SELECT * FROM list WHERE id=:id")
    ListModel getList(long id);

    @Query("SELECT * FROM list")
    List<ListModel> getAllList();

    @Query("SELECT * FROM list WHERE is_delete!=1 ORDER BY name ASC")
    List<ListModel> getAllActiveList();

    @Query("SELECT * FROM list WHERE is_delete=1")
    List<ListModel> getDeletedLists();

    @Query("SELECT * FROM list WHERE cost_category_id=:costCategoryId")
    List<ListModel> getListsForCostId(long costCategoryId);

    @Query("SELECT MAX(id) FROM list")
    long getLastId();
}
