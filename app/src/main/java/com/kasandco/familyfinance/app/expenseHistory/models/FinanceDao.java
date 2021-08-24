package com.kasandco.familyfinance.app.expenseHistory.models;

import androidx.room.Dao;
import androidx.room.Query;

import com.kasandco.familyfinance.dao.BaseDao;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface FinanceDao extends BaseDao<FinanceModel> {
    @Query("SELECT * FROM finance_history WHERE type=:type")
    Flowable<List<FinanceModel>> getAll(int type);
}
