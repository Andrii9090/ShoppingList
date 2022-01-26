package com.kasandco.familyfinance.app.finance.models;

import androidx.room.Dao;
import androidx.room.Query;

import com.kasandco.familyfinance.dao.BaseDao;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface FinanceCategoryDao extends BaseDao<FinanceCategoryModel> {

    @Query("SELECT *, (SELECT SUM(total) FROM finance_history as h WHERE h.category_id=fc.id OR h.category_id = fc.server_id AND h.date>=:dateStart AND h.date<=:dateEnd AND is_delete=0) as total FROM finance_category AS fc WHERE fc.type = :type AND fc.is_delete=0 ORDER BY fc.name ASC")
    Flowable<List<FinanceCategoryWithTotal>> getAll(int type, String dateStart, String dateEnd);

    @Query("SELECT * FROM finance_category WHERE type=1")
    List<FinanceCategoryModel> getAllCostCategory();

    @Query("SELECT * FROM finance_category")
    List<FinanceCategoryModel> getAllCategories();

    @Query("SELECT server_id FROM finance_category WHERE id=:id")
    long getServerId(long id);

    @Query("SELECT * FROM finance_category WHERE server_id=:serverId")
    FinanceCategoryModel getCategoryForServerId(long serverId);

    @Query("SELECT * FROM finance_category WHERE id=:financeCategoryId")
    FinanceCategoryModel getCategory(Long financeCategoryId);

    @Query("UPDATE finance_category SET is_delete=1 WHERE id=:financeCategoryId")
    void softDelete(long financeCategoryId);
}
