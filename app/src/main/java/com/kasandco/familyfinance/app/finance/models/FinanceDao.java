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
public interface FinanceDao extends BaseDao<FinanceHistoryModel> {
    @Query("SELECT * FROM finance_history WHERE type=:type")
    Flowable<List<FinanceHistoryModel>> getAllType(int type);

    @Query("SELECT server_id FROM finance_category WHERE id=:id")
    long getServerId(long id);

    @Query("SELECT * FROM finance_history")
    List<FinanceHistoryModel> getAll();

    @Query("SELECT SUM(total) FROM finance_history WHERE date>=:dateStart AND date<=:dateEnd and type=:type AND is_delete=0")
    Single<Double> getTotalToPeriod(int type, String dateStart, String dateEnd);

    @Query("SELECT SUM(total) FROM finance_history WHERE date>=:dateStart AND date<=:dateEnd and type=:type AND is_delete=0")
    Observable<Double> getTotal(int type, String dateStart, String dateEnd);

    @Query("SELECT SUM(fh.total) AS total, fc.name AS name FROM finance_history AS fh INNER JOIN finance_category AS fc ON (fh.category_id==fc.id) WHERE fh.date>=:dateStart AND fh.date<=:dateEnd AND fh.type=:type AND fh.is_delete=0 GROUP BY fc.name ORDER BY fh.total DESC")
    List<FinanceStatModel> getAllFromPeriod(int type, String dateStart, String dateEnd);

    @Query("SELECT * FROM  finance_history WHERE category_id=:category_id AND date>=:dateStart AND date<=:dateEnd ORDER BY date DESC")
    List<FinanceHistoryModel> getDetailFinance(long category_id, String dateStart, String dateEnd);

    @Query("SELECT * FROM finance_history WHERE server_id=:serverId")
    FinanceHistoryModel getForServerId(Long serverId);

    @Query("DELETE FROM finance_history WHERE category_id=:categoryId")
    void deleteFinanceHistory(long categoryId);

    @Query("UPDATE finance_history SET is_delete=1 WHERE category_id=:categoryId")
    void softDeleteFinanceHistory(long categoryId);
}
