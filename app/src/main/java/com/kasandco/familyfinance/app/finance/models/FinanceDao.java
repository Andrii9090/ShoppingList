package com.kasandco.familyfinance.app.finance.models;

import androidx.room.Dao;
import androidx.room.Query;

import com.kasandco.familyfinance.app.statistic.FinanceStatModel;
import com.kasandco.familyfinance.dao.BaseDao;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;


@Dao
public interface FinanceDao extends BaseDao<FinanceModel> {
    @Query("SELECT * FROM finance_history WHERE type=:type")
    Flowable<List<FinanceModel>> getAll(int type);

    @Query("SELECT SUM(total) FROM finance_history WHERE date>=:dateStart AND date<=:dateEnd and type=:type")
    Single<Double> getTotalToPeriod(int type, String dateStart, String dateEnd);

    @Query("SELECT SUM(total) FROM finance_history WHERE date>=:dateStart AND date<=:dateEnd and type=:type")
    Observable<Double> getTotal(int type, String dateStart, String dateEnd);

    @Query("SELECT SUM(fh.total) AS total, fc.name AS name FROM finance_history AS fh INNER JOIN finance_category AS fc ON (fh.category_id==fc.id) WHERE fh.date>=:dateStart AND fh.date<=:dateEnd AND fh.type=:type  GROUP BY fc.name ORDER BY fh.total DESC")
    List<FinanceStatModel> getAllFromPeriod(int type, String dateStart, String dateEnd);

    @Query("SELECT * FROM  finance_history WHERE category_id=:category_id AND date>=:dateStart AND date<=:dateEnd ORDER BY date DESC")
    List<FinanceModel> getDetailFinance(long category_id, String dateStart, String dateEnd);
}
