package com.kasandco.familyfinance.app.expenseHistory.models;

import androidx.room.Dao;
import androidx.room.Query;

import com.kasandco.familyfinance.app.statistic.FinanceStatModel;
import com.kasandco.familyfinance.dao.BaseDao;

import org.intellij.lang.annotations.Flow;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;


@Dao
public interface FinanceDao extends BaseDao<FinanceModel> {
    @Query("SELECT * FROM finance_history WHERE type=:type")
    Flowable<List<FinanceModel>> getAll(int type);

    @Query("SELECT SUM(total) FROM finance_history WHERE date>=:dateStart AND date<=:dateEnd and type=:type")
    Flowable<Double> getTotalToPeriod(int type, String dateStart, String dateEnd);

    @Query("SELECT SUM(fh.total) AS total, fc.name AS name, fc.id FROM finance_history AS fh INNER JOIN finance_category AS fc ON (fh.category_id==fc.id) WHERE fh.date>=:dateStart AND fh.date<=:dateEnd AND fh.type=:type  GROUP BY fc.name ORDER BY fh.total DESC")
    List<FinanceStatModel> getAllFromPeriod(int type, String dateStart, String dateEnd);
}
