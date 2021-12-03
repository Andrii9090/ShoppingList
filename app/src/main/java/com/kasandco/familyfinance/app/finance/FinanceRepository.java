package com.kasandco.familyfinance.app.finance;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Handler;
import android.os.Looper;

import com.kasandco.familyfinance.app.finance.models.FinanceCategoryDao;
import com.kasandco.familyfinance.app.finance.models.FinanceCategoryModel;
import com.kasandco.familyfinance.app.finance.models.FinanceCategorySync;
import com.kasandco.familyfinance.app.finance.models.FinanceCategorySyncDao;
import com.kasandco.familyfinance.app.finance.models.FinanceCategoryWithTotal;
import com.kasandco.familyfinance.app.finance.models.FinanceDao;
import com.kasandco.familyfinance.app.finance.models.FinanceHistoryModel;
import com.kasandco.familyfinance.app.finance.models.FinanceHistorySync;
import com.kasandco.familyfinance.app.finance.models.FinanceHistorySyncDao;
import com.kasandco.familyfinance.app.list.ListDao;
import com.kasandco.familyfinance.app.list.ListModel;
import com.kasandco.familyfinance.core.AppDataBase;
import com.kasandco.familyfinance.core.BaseRepository;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.network.FinanceNetworkInterface;
import com.kasandco.familyfinance.network.ListNetworkInterface;
import com.kasandco.familyfinance.network.Requests;
import com.kasandco.familyfinance.network.model.FinanceCategoryApiModel;
import com.kasandco.familyfinance.network.model.FinanceHistoryApiModel;
import com.kasandco.familyfinance.network.model.LastSyncApiDataModel;
import com.kasandco.familyfinance.network.model.ListApiModel;
import com.kasandco.familyfinance.utils.IsNetworkConnect;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FinanceRepository extends BaseRepository {
    private final ListDao listDao;
    private final FinanceCategoryDao financeCategoryDao;
    private final FinanceDao financeHistoryDao;
    private CompositeDisposable disposable;
    private FinanceNetworkInterface network;
    private final Retrofit retrofit;
    private final FinanceCategorySyncDao financeCategorySyncDao;
    private final FinanceHistorySyncDao financeHistorySyncDao;

    public FinanceRepository(AppDataBase appDataBase, Retrofit _retrofit, SharedPreferenceUtil _sharedPreference, IsNetworkConnect _isNetworkConnect) {
        super(_sharedPreference, _isNetworkConnect);
        disposable = new CompositeDisposable();

        financeHistoryDao = appDataBase.getFinanceDao();
        financeCategoryDao = appDataBase.getFinanceCategoryDao();
        listDao = appDataBase.getListDao();
        financeCategorySyncDao = appDataBase.getFinanceCategorySyncDao();
        financeHistorySyncDao = appDataBase.getFinanceHistorySyncDao();

        network = _retrofit.create(FinanceNetworkInterface.class);
        retrofit = _retrofit;
    }

    public void createNewCategory(FinanceCategoryModel category, FinanceRepositoryCallback callback, boolean checked) {
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            try {
                long id = financeCategoryDao.insert(category);
                handler.post(() -> callback.added(id));
                category.setId(id);
                createNewCategoryNetwork(category);
            } catch (SQLiteConstraintException e) {
                handler.post(callback::notUnique);
            }
        }).start();
    }

    private void createNewCategoryNetwork(FinanceCategoryModel category) {
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
            FinanceCategoryApiModel responseModel = new FinanceCategoryApiModel(category);
            Requests.RequestsInterface<FinanceCategoryApiModel> callbackResponse = new Requests.RequestsInterface<FinanceCategoryApiModel>() {
                @Override
                public void success(FinanceCategoryApiModel responseObj) {
                    category.setServerId(responseObj.getId());
                    category.setDateMod(responseObj.getDateMod());
                    category.setDateModServer(responseObj.getDateMod());
                    new Thread(() -> financeCategoryDao.update(category)).start();
                }

                @Override
                public void error() {

                }
            };

            Call<FinanceCategoryApiModel> call = network.createCategory(responseModel);
            Requests.request(call, callbackResponse);
        }
    }

    public void createNewList(long categoryId, FinanceCategoryModel category) {
        ListModel list = new ListModel(category.getName(), String.valueOf(System.currentTimeMillis()), category.getIconPath(), categoryId);
        new Thread(() -> {
            list.setId(listDao.insert(list));
            createNetworkNewList(list);
        }).start();
    }

    private void createNetworkNewList(ListModel list) {
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
            ListNetworkInterface listNetwork = retrofit.create(ListNetworkInterface.class);
            ListApiModel listData = new ListApiModel(list);

            Requests.RequestsInterface<ListApiModel> callbackResponse = new Requests.RequestsInterface<ListApiModel>() {
                @Override
                public void success(ListApiModel responseObj) {
                    if (responseObj != null) {
                        list.setServerId(responseObj.getId());
                        list.setDateMod(responseObj.getDateMod());
                        new Thread(() -> listDao.update(list)).start();
                    }
                }

                @Override
                public void error() {

                }
            };

            Call<ListApiModel> call = listNetwork.createNewList(listData);
            Requests.request(call, callbackResponse);
        }
    }

    public void getAllData(int type, String dateStart, String dateEnd, FinanceHistoryCallback callback) {
        disposable.add(financeCategoryDao.getAll(type, dateStart, dateEnd)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::setAllItems));
        sync();
    }

    private void sync() {
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
            Thread updateLocal = new Thread(() -> {
                List<FinanceCategoryModel> allCategories = financeCategoryDao.getAllCategories();
                for (FinanceCategoryModel category : allCategories) {
                    if (category.getIsDelete() == 1) {
                        remove(category);
                    } else if (category.getDateModServer() != null && !category.getDateMod().equals(category.getDateModServer())) {
                        updateNetworkCategory(category);
                    } else if (category.getServerId() == 0) {
                        createNewCategoryNetwork(category);
                    }
                }
            });
            new Thread(() -> {
                List<FinanceCategorySync> financeCategorySyncs = financeCategorySyncDao.getAll();
                List<LastSyncApiDataModel> syncData = new ArrayList<>();
                for (FinanceCategorySync item : financeCategorySyncs) {
                    syncData.add(new LastSyncApiDataModel(item.getServerId(), item.getDateMod()));
                }
                Requests.RequestsInterface<List<FinanceCategoryApiModel>> callbackResponse = new Requests.RequestsInterface<List<FinanceCategoryApiModel>>() {
                    @Override
                    public void success(List<FinanceCategoryApiModel> responseObj) {
                        if (responseObj != null && responseObj.size() > 0) {
                            new Thread(() -> {
                                financeCategorySyncDao.clearAll();
                                for (FinanceCategoryApiModel responseItem : responseObj) {
                                    financeCategorySyncDao.insert(new FinanceCategorySync(responseItem));
                                    FinanceCategoryModel categoryModel = financeCategoryDao.getCategoryForServerId(responseItem.getId());
                                    if (categoryModel != null) {
                                        FinanceCategoryModel categoryModelModify = new FinanceCategoryModel(responseItem);
                                        categoryModelModify.setId(categoryModel.getId());
                                        if (responseItem.getIsDelete()) {
                                            financeCategoryDao.delete(categoryModelModify);
                                        } else {
                                            financeCategoryDao.update(categoryModelModify);
                                        }
                                    } else {
                                        financeCategoryDao.insert(new FinanceCategoryModel(responseItem));
                                    }
                                }
                            }).start();
                        }
                        updateLocal.start();
                        syncFinanceHistoryItems();
                    }

                    @Override
                    public void error() {
                        updateLocal.start();
                    }
                };
                Call<List<FinanceCategoryApiModel>> call = network.syncCategory(syncData, deviceId);

                Requests.request(call, callbackResponse);
            }).start();
        }
    }


    private void syncFinanceHistoryItems() {

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
                    };
                    Call<ResponseBody> call = network.removeHistoryItem(modelItem.getServerId());

                    Requests.request(call, callbackDelete);
                }
            }
        });

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
                                        model.setIsDelete(item.is_delete()?1:0);
                                        financeHistoryDao.insert(model);
                                    }
                                }
                            }
                        }).start();
                    }
                    updateLocalThread.start();
                }

                @Override
                public void error() {
                    updateLocalThread.start();
                }
            };
            Requests.request(call, callbackResponse);
        }).start();

    }

    public void getAllCostCategory(AllCostCategoryCallback callback) {
        Handler handler = new Handler();
        new Thread(() -> {
            List<FinanceCategoryModel> lists = financeCategoryDao.getAllCostCategory();
            handler.post(() -> {
                callback.setAllCostCategory(lists);
            });
        }).start();
    }

    public void clearDisposable() {
        disposable.dispose();
    }

    public void updateFinanceCategory(FinanceCategoryModel editItem, FinanceRepositoryCallback callback) {
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            financeCategoryDao.update(editItem);
            handler.post(() -> callback.added(0));
        }).start();
        updateNetworkCategory(editItem);
    }

    private void updateNetworkCategory(FinanceCategoryModel item) {
        FinanceCategoryApiModel categoryNetworkModel = new FinanceCategoryApiModel(item);
        Requests.RequestsInterface<FinanceCategoryApiModel> callbackResponse = new Requests.RequestsInterface<FinanceCategoryApiModel>() {
            @Override
            public void success(FinanceCategoryApiModel responseObj) {
                if (responseObj != null) {
                    item.setDateMod(responseObj.getDateMod());
                    item.setDateModServer(responseObj.getDateMod());
                    new Thread(() -> financeCategoryDao.update(item));
                }
            }

            @Override
            public void error() {

            }
        };

        Call<FinanceCategoryApiModel> call = network.updateCategory(categoryNetworkModel);
        Requests.request(call, callbackResponse);
    }

    public void remove(FinanceCategoryModel financeCategory) {
        if (isLogged) {
            if (isNetworkConnect.isInternetAvailable() && financeCategory.getServerId() > 0) {
                Requests.RequestsInterface<ResponseBody> callbackResponse = new Requests.RequestsInterface<ResponseBody>() {
                    @Override
                    public void success(ResponseBody responseObj) {
                        new Thread(() -> {
                            financeHistoryDao.deleteFinanceHistory(financeCategory.getId());
                            financeCategory.setDateMod(String.valueOf(System.currentTimeMillis()));
                            financeCategoryDao.delete(financeCategory);
                        }).start();
                    }

                    @Override
                    public void error() {
                        softRemove(financeCategory);
                    }
                };

                Call<ResponseBody> call = network.removeCategory(financeCategory.getServerId());
                Requests.request(call, callbackResponse);
            } else {
                softRemove(financeCategory);
            }
        } else {
            new Thread(() -> {
                financeCategory.setDateMod(String.valueOf(System.currentTimeMillis()));
                financeHistoryDao.deleteFinanceHistory(financeCategory.getId());
                financeCategoryDao.delete(financeCategory);
            }).start();
        }
    }

    private void softRemove(FinanceCategoryModel financeCategory) {
        financeCategory.setIsDelete(1);
        financeCategory.setDateMod(String.valueOf(System.currentTimeMillis()));
        new Thread(() -> {
            financeHistoryDao.softDeleteFinanceHistory(financeCategory.getId());
            financeCategoryDao.update(financeCategory);
        }).start();
    }

    public void createNewHistoryItem(FinanceHistoryModel item) {
        new Thread(() -> {
            item.setId(financeHistoryDao.insert(item));
            long serverFinanceCategory = financeCategoryDao.getServerId(item.getCategoryId());
            FinanceHistoryApiModel apiModel = new FinanceHistoryApiModel(null, item.getComment(), item.getTotal(), item.getDate(), item.getUserEmail(), item.getType(), serverFinanceCategory, item.getId(), item.getIsDelete() == 1, item.getDateMod());
            Requests.RequestsInterface<FinanceHistoryApiModel> callbackResponse = new Requests.RequestsInterface<FinanceHistoryApiModel>() {
                @Override
                public void success(FinanceHistoryApiModel responseObj) {
                    if (responseObj != null) {
                        item.setDateMod(responseObj.getDate_mod());
                        item.setDateModServer(responseObj.getDate_mod());
                        item.setServerCategoryId(responseObj.getFinance_category());
                        item.setServerId(responseObj.getId());
                        new Thread(() -> financeHistoryDao.update(item)).start();
                    }
                }

                @Override
                public void error() {

                }
            };

            Call<FinanceHistoryApiModel> call = network.createItemHistory(apiModel);
            Requests.request(call, callbackResponse);
        }).start();
    }

    public void getTotalToPeriod(String startDate, String endDate, FinanceTotalResult callback) {
        disposable.add(financeHistoryDao.getTotalToPeriod(1, startDate, endDate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(
                        aDouble -> callback.setTotal(1, aDouble),
                        throwable -> callback.setTotal(1, 0.0f)));
        disposable.add(financeHistoryDao.getTotalToPeriod(2, startDate, endDate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(aDouble -> callback.setTotal(2, aDouble), throwable -> callback.setTotal(2, 0.0f)));

        disposable.add(financeHistoryDao.getTotal(1, startDate, endDate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(
                        aDouble -> callback.setTotal(1, aDouble),
                        throwable -> callback.setTotal(1, 0.0f)));
        disposable.add(financeHistoryDao.getTotal(2, startDate, endDate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(aDouble -> callback.setTotal(2, aDouble), throwable -> callback.setTotal(2, 0.0f)));
    }


    public interface FinanceRepositoryCallback {
        void notUnique();

        void added(long id);

    }

    public interface FinanceHistoryCallback {
        void setAllItems(List<FinanceCategoryWithTotal> historyList);
    }

    public interface FinanceTotalResult {
        void setTotal(int type, double res);
    }

    public interface AllCostCategoryCallback {
        void setAllCostCategory(List<FinanceCategoryModel> categoryModels);
    }

}
