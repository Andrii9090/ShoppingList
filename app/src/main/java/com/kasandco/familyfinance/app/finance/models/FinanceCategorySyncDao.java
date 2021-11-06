package com.kasandco.familyfinance.app.finance.models;

import androidx.room.Dao;
import androidx.room.Query;

import com.kasandco.familyfinance.dao.BaseDao;

import java.util.List;

@Dao
public interface FinanceCategorySyncDao extends BaseDao<FinanceCategorySync> {
    @Query("DELETE FROM finance_category_sync")
    void  clearAll();

    @Query("SELECT * FROM finance_category_sync")
    List<FinanceCategorySync> getAll();
}
