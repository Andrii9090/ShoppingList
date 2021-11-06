package com.kasandco.familyfinance.app.finance;

import android.os.Handler;
import android.os.Looper;

import com.kasandco.familyfinance.app.finance.models.FinanceDao;
import com.kasandco.familyfinance.app.finance.models.FinanceHistoryModel;
import com.kasandco.familyfinance.app.finance.models.FinanceHistorySync;
import com.kasandco.familyfinance.app.finance.models.FinanceHistorySyncDao;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.network.FinanceNetworkInterface;
import com.kasandco.familyfinance.network.model.FinanceHistoryApiModel;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@FinanceDetailScope
public class FinanceDetailRepository {
    private FinanceDao financeHistoryDao;
    private FinanceHistorySyncDao financeHistorySyncDao;
    private FinanceNetworkInterface network;
    private SharedPreferenceUtil sharedPreference;

    @Inject
    public FinanceDetailRepository(FinanceDao _financeHistoryDao, FinanceHistorySyncDao _financeHistorySyncDao, Retrofit _retrofit, SharedPreferenceUtil _sharedPreference) {
        financeHistoryDao = _financeHistoryDao;
        financeHistorySyncDao = _financeHistorySyncDao;
        network = _retrofit.create(FinanceNetworkInterface.class);
        sharedPreference = _sharedPreference;
    }

    public void getFinancesDetail(long category_id, String dateStart, String dateEnd, FinanceDetailCallback callback) {
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            List<FinanceHistoryModel> items = financeHistoryDao.getDetailFinance(category_id, dateStart, dateEnd);
            handler.post(() -> {
                callback.setFinanceItems(items);
            });
        }).start();
    }

    public interface FinanceDetailCallback {
        void setFinanceItems(List<FinanceHistoryModel> items);
    }
}
