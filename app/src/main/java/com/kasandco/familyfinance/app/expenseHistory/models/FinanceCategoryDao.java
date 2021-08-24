package com.kasandco.familyfinance.app.expenseHistory.models;

import androidx.room.Dao;
import androidx.room.Query;

import com.kasandco.familyfinance.dao.BaseDao;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface FinanceCategoryDao extends BaseDao<FinanceCategoryModel> {

    @Query("SELECT * FROM finance_category ORDER BY id DESC LIMIT 1")
    Single<FinanceCategoryModel> getLastRow();

    @Query("SELECT *, (SELECT SUM(total) FROM finance_history as h WHERE h.date>=:dateStart AND h.date<=:dateEnd AND h.category_id=fc.id) as total FROM finance_category AS fc WHERE fc.type = :type ORDER BY fc.name DESC")
    Flowable<List<FinanceCategoryWithTotal>> getAll(int type, String dateStart, String dateEnd);
}
