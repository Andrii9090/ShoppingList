package com.kasandco.familyfinance.app.finance;

import android.os.Handler;
import android.os.Looper;

import com.kasandco.familyfinance.app.finance.core.FinanceDetailScope;
import com.kasandco.familyfinance.app.finance.models.FinanceCategoryDao;
import com.kasandco.familyfinance.app.finance.models.FinanceCategoryModel;
import com.kasandco.familyfinance.app.finance.models.FinanceDao;
import com.kasandco.familyfinance.app.finance.models.FinanceDetailModel;
import com.kasandco.familyfinance.app.finance.models.FinanceHistoryModel;
import com.kasandco.familyfinance.app.finance.models.FinanceHistorySync;
import com.kasandco.familyfinance.app.finance.models.FinanceHistorySyncDao;
import com.kasandco.familyfinance.core.BaseRepository;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.network.FinanceNetworkInterface;
import com.kasandco.familyfinance.network.Requests;
import com.kasandco.familyfinance.network.model.FinanceHistoryApiModel;
import com.kasandco.familyfinance.utils.NetworkConnect;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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
    private FinanceDetailCallback callback;
    private FinanceCategoryDao financeCategoryDao;

    @Inject
    public FinanceDetailRepository(FinanceDao _financeHistoryDao, FinanceHistorySyncDao _financeHistorySyncDao, Retrofit _retrofit, SharedPreferenceUtil _sharedPreference, NetworkConnect _isNetworkConnect, FinanceCategoryDao _financeCategoryDao) {
        super(_sharedPreference, _isNetworkConnect);
        financeHistoryDao = _financeHistoryDao;
        financeHistorySyncDao = _financeHistorySyncDao;
        network = _retrofit.create(FinanceNetworkInterface.class);
        sharedPreference = _sharedPreference;
        financeCategoryDao = _financeCategoryDao;
    }

    public void getFinancesDetail(long category_id, String dateStart, String dateEnd, FinanceDetailCallback callback) {
        Handler handler = new Handler(Looper.getMainLooper());
        this.callback = callback;
        if(isLogged && isNetworkConnect.isInternetAvailable()){
            sync(category_id, dateStart, dateEnd, callback);
        }else {
            getLocal(category_id, dateStart, dateEnd, callback, handler);
        }
    }

    private void getLocal(long category_id, String dateStart, String dateEnd, FinanceDetailCallback callback, Handler handler) {

        new Thread(() -> {
            Long catId = category_id;
            FinanceCategoryModel financeCategoryModel = financeCategoryDao.getCategory(category_id);
            if(financeCategoryModel.getServerId()>0){
                catId = financeCategoryModel.getServerId();
            }
            List<FinanceHistoryModel> items = financeHistoryDao.getDetailFinance(catId.longValue(), dateStart, dateEnd);
            handler.post(() -> {
                callback.setFinanceItems(items);
            });
        }).start();
    }

    public void removeItem(FinanceDetailModel item) {
        if (isLogged) {
            if(isNetworkConnect.isInternetAvailable()) {
                Handler handler = new Handler();
                new Thread(() -> {
                    FinanceHistoryModel model = financeHistoryDao.getForServerId(item.getServerId());
                    if (isNetworkConnect.isInternetAvailable()) {
                        Requests.RequestsInterface<ResponseBody> callbackDelete = new Requests.RequestsInterface<ResponseBody>() {
                            @Override
                            public void success(ResponseBody responseObj) {
                                new Thread(() -> financeHistoryDao.delete(model)).start();
                                handler.post(()->callback.removed(item));
                            }

                            @Override
                            public void error() {
                                handler.post(()->callback.permDined());
                            }

                            @Override
                            public void noPermit() {

                            }
                        };
                        Call<ResponseBody> call = network.removeHistoryItem(item.getServerId());
                        Requests.request(call, callbackDelete);
                    }
                }).start();
            }else{
                new Thread(()->financeHistoryDao.softDeleteFinanceHistory(item.getId())).start();
            }
        }else {
            new Thread(()->{
                FinanceHistoryModel model = financeHistoryDao.getForServerId(item.getServerId());
                financeHistoryDao.delete(model);
                callback.removed(item);
            });
        }
    }

    private void sync(long category_id, String dateStart, String dateEnd, FinanceDetailCallback callback) {
        Thread updateLocalThread = new Thread(() -> {
            List<FinanceHistoryModel> items = financeHistoryDao.getAll();

            for (FinanceHistoryModel modelItem : items) {
                if (modelItem.getIsDelete() == 1) {
                    Requests.RequestsInterface<ResponseBody> callbackDelete = new Requests.RequestsInterface<ResponseBody>() {
                        @Override
                        public void success(ResponseBody responseObj) {
                            new Thread(() -> financeHistoryDao.delete(modelItem)).start();
                        }

                        @Override
                        public void error() {

                        }

                        @Override
                        public void noPermit() {

                        }
                    };
                    Call<ResponseBody> call = network.removeHistoryItem(modelItem.getServerId());

                    Requests.request(call, callbackDelete);
                }
            }
        });
        Handler handler = new Handler();
        new Thread(() -> {
            List<FinanceHistorySync> lastSyncData = financeHistorySyncDao.getAll();

            Call<List<FinanceHistoryApiModel>> call = network.syncHistory(lastSyncData, sharedPreference.getSharedPreferences().getString(Constants.DEVICE_ID, ""));

            Requests.RequestsInterface<List<FinanceHistoryApiModel>> callbackResponse = new Requests.RequestsInterface<List<FinanceHistoryApiModel>>() {
                @Override
                public void success(List<FinanceHistoryApiModel> responseObj) {
                    new Thread(financeHistorySyncDao::clear).start();
                    if (responseObj != null && responseObj.size() > 0) {
                        new Thread(() -> {
                            for (FinanceHistoryApiModel item : responseObj) {
                                financeHistorySyncDao.insert(new FinanceHistorySync(0, item.getId(), item.getDate_mod()));
                                FinanceHistoryModel historyModel = financeHistoryDao.getForServerId(item.getId());
                                FinanceHistoryModel modelModify = new FinanceHistoryModel(item);
                                if (historyModel != null) {
                                    modelModify.setId(historyModel.getId());
                                    modelModify.setCategoryId(historyModel.getCategoryId());
                                    if (item.is_delete()) {
                                        financeHistoryDao.delete(modelModify);
                                    } else {
                                        financeHistoryDao.update(modelModify);
                                    }
                                } else {
                                    FinanceCategoryModel financeCategory = financeCategoryDao.getCategoryForServerId(item.getFinance_category());
                                    if (financeCategory != null) {
                                        FinanceHistoryModel model = new FinanceHistoryModel(item);
                                        model.setCategoryId(financeCategory.getId());
                                        model.setIsDelete(item.is_delete() ? 1 : 0);
                                        financeHistoryDao.insert(model);
                                    }
                                }
                            }
                        }).start();
                    }
                    updateLocalThread.start();
                    getLocal(category_id, dateStart,dateEnd,callback,handler);
                }

                @Override
                public void error() {
                    getLocal(category_id, dateStart,dateEnd,callback,handler);
                }

                @Override
                public void noPermit() {

                }
            };
            Requests.request(call, callbackResponse);
        }).start();

    }

    public interface FinanceDetailCallback {
        void setFinanceItems(List<FinanceHistoryModel> items);
        void permDined();

        void removed(FinanceDetailModel item);
    }
}
