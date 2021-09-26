package com.kasandco.familyfinance.app.finance;

import android.os.Handler;
import android.os.Looper;

import com.kasandco.familyfinance.app.finance.models.FinanceDao;
import com.kasandco.familyfinance.app.finance.models.FinanceModel;

import java.util.List;

import javax.inject.Inject;

@FinanceDetailScope
public class FinanceDetailRepository {
    FinanceDao financeHistoryDao;


    @Inject
    public FinanceDetailRepository(FinanceDao _financeHistoryDao){
        financeHistoryDao = _financeHistoryDao;
    }

    public void getFinancesDetail(long category_id, String dateStart, String dateEnd, FinanceDetailCallback callback){
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(()->{
            List<FinanceModel> items = financeHistoryDao.getDetailFinance(category_id, dateStart, dateEnd);
            handler.post(()->{
                callback.setFinanceItems(items);
            });
        }).start();
    }


    public interface FinanceDetailCallback {
        void setFinanceItems(List<FinanceModel> items);
    }
}
