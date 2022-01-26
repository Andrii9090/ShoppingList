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
import com.kasandco.familyfinance.utils.NetworkConnect;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
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
    private FinanceHistoryCallback callback;

    public FinanceRepository(AppDataBase appDataBase, Retrofit _retrofit, SharedPreferenceUtil _sharedPreference, NetworkConnect _isNetworkConnect) {
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

    public void createNewCategory(FinanceCategoryModel category, FinanceRepositoryCallback callback) {
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            try {
                long id = financeCategoryDao.insert(category);
                category.setId(id);
                createNewCategoryNetwork(category, callback, handler);
            } catch (SQLiteConstraintException e) {
                handler.post(callback::notUnique);
            }
        }).start();
    }

    private void createNewCategoryNetwork(FinanceCategoryModel category, FinanceRepositoryCallback callbackPresenter, Handler handler) {
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
            FinanceCategoryApiModel responseModel = new FinanceCategoryApiModel(category);
            Requests.RequestsInterface<FinanceCategoryApiModel> callbackResponse = new Requests.RequestsInterface<FinanceCategoryApiModel>() {
                @Override
                public void success(FinanceCategoryApiModel responseObj) {
                    category.setServerId(responseObj.getId());
                    category.setDateMod(responseObj.getDateMod());
                    category.setDateModServer(responseObj.getDateMod());
                    category.setIsOwner(responseModel.isOwner() ? 1 : 0);
                    new Thread(() -> {
                        financeCategoryDao.update(category);
                        if (handler != null)
                            handler.post(() -> callbackPresenter.added(category.getId()));
                    }).start();
                }

                @Override
                public void error() {

                }

                @Override
                public void noPermit() {
                    if (callback != null) {
                        callback.noPerm();
                    }
                }
            };

            Call<FinanceCategoryApiModel> call = network.createCategory(responseModel);
            Requests.request(call, callbackResponse);
        } else {
            if (handler != null)
                handler.post(() -> callbackPresenter.added(category.getId()));
        }
    }

    public void createNewList(long categoryId, FinanceCategoryModel category, FinanceRepositoryCallback callback) {
        ListModel list = new ListModel(category.getName(), String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), category.getIconPath(), categoryId);
        Handler handler = new Handler();
        new Thread(() -> {
            if (!isLogged && listDao.getQuantity() >= Constants.MAX_QUANTITY_WITHOUT_REG) {
                handler.post(callback::maxLimit);
            } else {
                long id = listDao.insert(list);
                list.setId(id);
                createNetworkNewList(list);
            }
        }).start();
    }

    private void createNetworkNewList(ListModel list) {
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
            ListApiModel listData = new ListApiModel(list);
            if (listData.getFinanceCategoryId() != 0) {
                long categoryId = financeCategoryDao.getServerId(listData.getFinanceCategoryId());
                listData.setFinanceCategoryId(categoryId);
                list.setServerFinanceCategoryId(categoryId);
            }

            ListNetworkInterface listNetwork = retrofit.create(ListNetworkInterface.class);
            Requests.RequestsInterface<ListApiModel> callbackResponse = new Requests.RequestsInterface<ListApiModel>() {
                @Override
                public void success(ListApiModel responseObj) {
                    if (responseObj != null) {
                        list.setServerId(responseObj.getId());
                        list.setServerFinanceCategoryId(responseObj.getFinanceCategoryId() == null ? 0 : responseObj.getFinanceCategoryId());
                        list.setDateMod(responseObj.getDateMod());
                        new Thread(() -> listDao.update(list)).start();
                    }
                }

                @Override
                public void error() {

                }

                @Override
                public void noPermit() {
                    if (callback != null) {
                        callback.noPerm();
                    }
                }
            };

            Call<ListApiModel> call = listNetwork.createNewList(listData);
            Requests.request(call, callbackResponse);
        }
    }

    public void getAllData(int type, String dateStart, String dateEnd, FinanceHistoryCallback callback) {
        this.callback = callback;
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
                        createNewCategoryNetwork(category, null, null);
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
                                        if (responseItem.getIsDelete() || responseItem.isPrivate() && !responseItem.isOwner()) {
                                            financeCategoryDao.delete(categoryModelModify);
                                        } else {
                                            financeCategoryDao.update(categoryModelModify);
                                        }

                                    } else {
                                        if ((!responseItem.getIsDelete() && !responseItem.isPrivate()) || (!responseItem.getIsDelete() && responseItem.isPrivate() && responseItem.isOwner())) {
                                            try {
                                                financeCategoryDao.insert(new FinanceCategoryModel(responseItem));
                                            } catch (SQLiteConstraintException e) {
                                                continue;
                                            }
                                        }
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

                    @Override
                    public void noPermit() {
                        if (callback != null) {
                            callback.noPerm();
                        }
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

                        @Override
                        public void noPermit() {
                            if (callback != null) {
                                callback.noPerm();
                            }
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
                                        model.setIsDelete(item.is_delete() ? 1 : 0);
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

                @Override
                public void noPermit() {
                    if (callback != null) {
                        callback.noPerm();
                    }
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
        updateNetworkCategory(editItem);
        new Thread(() -> {
            financeCategoryDao.update(editItem);
            if (callback != null) {
                handler.post(() -> callback.added(editItem.getId()));
            }
        }).start();
    }

    private void updateNetworkCategory(FinanceCategoryModel item) {
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
            FinanceCategoryApiModel categoryNetworkModel = new FinanceCategoryApiModel(item);
            Requests.RequestsInterface<FinanceCategoryApiModel> callbackResponse = new Requests.RequestsInterface<FinanceCategoryApiModel>() {
                @Override
                public void success(FinanceCategoryApiModel responseObj) {
                    if (responseObj != null) {
                        item.setDateMod(responseObj.getDateMod());
                        item.setDateModServer(responseObj.getDateMod());
                        new Thread(() -> financeCategoryDao.update(item)).start();
                    }
                }

                @Override
                public void error() {
                    callback.error();
                }

                @Override
                public void noPermit() {
                    if (callback != null) {
                        callback.noPerm();
                    }
                }
            };

            Call<FinanceCategoryApiModel> call = network.updateCategory(categoryNetworkModel);
            Requests.request(call, callbackResponse);
        } else {
            callback.noInternet();
        }
    }

    public void remove(FinanceCategoryModel financeCategory) {
        new Thread(() -> {
            financeCategory.setDateMod(String.valueOf(System.currentTimeMillis()));
            financeHistoryDao.softDeleteFinanceHistory(financeCategory.getId());
            financeCategoryDao.softDelete(financeCategory.getId());
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
                            callback.error();
                        }

                        @Override
                        public void noPermit() {
                            if (callback != null) {
                                callback.noPerm();
                            }
                        }
                    };

                    Call<ResponseBody> call = network.removeCategory(financeCategory.getServerId());
                    Requests.request(call, callbackResponse);
                }
            } else {
                financeCategory.setDateMod(String.valueOf(System.currentTimeMillis()));
                financeHistoryDao.deleteFinanceHistory(financeCategory.getId());
                financeCategoryDao.delete(financeCategory);
            }
        }).start();

    }

    public void createNewHistoryItem(int type, long categoryId, long serverCategoryId, String amount, String comment, GregorianCalendar selectedDate) {

        new Thread(() -> {
            FinanceHistoryModel item = new FinanceHistoryModel(String.valueOf(selectedDate.getTime().getTime()), categoryId, serverCategoryId, Double.parseDouble(amount), comment, type, String.valueOf(System.currentTimeMillis()), "");

            item.setId(financeHistoryDao.insert(item));
            createNetworkNewHistory(item);
        }).start();
    }

    private void createNetworkNewHistory(FinanceHistoryModel item) {
        if(isLogged && isNetworkConnect.isInternetAvailable()) {
            FinanceHistoryApiModel apiModel = new FinanceHistoryApiModel(null, item.getComment(), item.getTotal(), item.getDate(), item.getUserEmail(), item.getType(), item.getServerCategoryId(), item.getId(), item.getIsDelete() == 1, item.getDateMod());
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

                @Override
                public void noPermit() {
                    if (callback != null) {
                        callback.noPerm();
                    }
                }
            };

            Call<FinanceHistoryApiModel> call = network.createItemHistory(apiModel);
            Requests.request(call, callbackResponse);
        }
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

    public void setPrivate(FinanceCategoryWithTotal financeCategory) {
        if (financeCategory.getCategory().getIsPrivate() == 1) {
            financeCategory.getCategory().setIsPrivate(0);
        } else {
            financeCategory.getCategory().setIsPrivate(1);
        }
        updateFinanceCategory(financeCategory.getCategory(), null);
    }


    public interface FinanceRepositoryCallback {
        void notUnique();

        void added(long id);

        void maxLimit();
    }

    public interface FinanceHistoryCallback {
        void setAllItems(List<FinanceCategoryWithTotal> historyList);

        void noPerm();

        void error();

        void noInternet();
    }

    public interface FinanceTotalResult {
        void setTotal(int type, double res);
    }

    public interface AllCostCategoryCallback {
        void setAllCostCategory(List<FinanceCategoryModel> categoryModels);
    }

}
