package com.kasandco.familyfinance.app.finance;

import android.os.Handler;
import android.os.Looper;

import com.kasandco.familyfinance.app.finance.core.FinanceDetailScope;
import com.kasandco.familyfinance.app.finance.models.FinanceDao;
import com.kasandco.familyfinance.app.finance.models.FinanceDetailModel;
import com.kasandco.familyfinance.app.finance.models.FinanceHistoryModel;
import com.kasandco.familyfinance.app.finance.models.FinanceHistorySyncDao;
import com.kasandco.familyfinance.core.BaseRepository;
import com.kasandco.familyfinance.network.FinanceNetworkInterface;
import com.kasandco.familyfinance.network.Requests;
import com.kasandco.familyfinance.utils.IsNetworkConnect;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

@FinanceDetailScope
public class FinanceDetailRepository extends BaseRepository {
    private FinanceDao financeHistoryDao;
    private FinanceHistorySyncDao financeHistorySyncDao;
    private FinanceNetworkInterface network;
    private SharedPreferenceUtil sharedPreference;

    @Inject
    public FinanceDetailRepository(FinanceDao _financeHistoryDao, FinanceHistorySyncDao _financeHistorySyncDao, Retrofit _retrofit, SharedPreferenceUtil _sharedPreference, IsNetworkConnect _isNetworkConnect) {
        super(_sharedPreference, _isNetworkConnect);
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

    public void removeItem(FinanceDetailModel item) {
        if (isLogged) {
            new Thread(() -> {
                FinanceHistoryModel model = financeHistoryDao.getForServerId(item.getServerId());
                if (isNetworkConnect.isInternetAvailable()) {
                    Requests.RequestsInterface<ResponseBody> callbackDelete = new Requests.RequestsInterface<ResponseBody>() {
                        @Override
                        public void success(ResponseBody responseObj) {
                            new Thread(() -> financeHistoryDao.delete(model)).start();
                        }

                        @Override
                        public void error() {
                            softDelete(model);
                        }
                    };
                    Call<ResponseBody> call = network.removeHistoryItem(item.getServerId());
                    Requests.request(call, callbackDelete);
                }else {
                    softDelete(model);
                }
            }).start();
        }else {
            new Thread(()->{
                FinanceHistoryModel model = financeHistoryDao.getForServerId(item.getServerId());
                financeHistoryDao.delete(model);
            });
        }
    }

    private void softDelete(FinanceHistoryModel item) {
        new Thread(() -> financeHistoryDao.softDeleteFinanceHistory(item.getId())).start();
    }

    public interface FinanceDetailCallback {
        void setFinanceItems(List<FinanceHistoryModel> items);
    }
}
