package com.kasandco.familyfinance.app.statistic;

import android.os.Handler;
import android.os.Looper;

import com.kasandco.familyfinance.app.finance.models.FinanceDao;

import java.util.List;

public class StatisticRepository {
    private FinanceDao financeDao;

    public StatisticRepository(FinanceDao financeDao){
        this.financeDao = financeDao;
    }

    public void getAllFromPeriod(int type, String dateStart, String dateEnd, RepositoryCallback callback){
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            List<FinanceStatModel> statDb = financeDao.getAllFromPeriod(type, dateStart, dateEnd);
            handler.post(() -> callback.loadedData(statDb));
        }).start();
    }

    public interface RepositoryCallback {
        void loadedData(List<FinanceStatModel> statDb);
    }
}
