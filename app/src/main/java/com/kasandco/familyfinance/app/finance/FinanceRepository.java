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
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.network.FinanceNetworkInterface;
import com.kasandco.familyfinance.network.ListNetworkInterface;
import com.kasandco.familyfinance.network.model.FinanceCategoryApiModel;
import com.kasandco.familyfinance.network.model.FinanceHistoryApiModel;
import com.kasandco.familyfinance.network.model.LastSyncApiDataModel;
import com.kasandco.familyfinance.network.model.ListDataApiModel;
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

public class FinanceRepository {
    private ListDao listDao;
    private FinanceCategoryDao financeCategoryDao;
    private FinanceDao financeHistoryDao;
    private CompositeDisposable disposable;
    private SharedPreferenceUtil sharedPreference;
    private FinanceNetworkInterface network;
    private Retrofit retrofit;
    private IsNetworkConnect networkConnect;
    private FinanceCategorySyncDao financeCategorySyncDao;
    private FinanceHistorySyncDao financeHistorySyncDao;

    public FinanceRepository(AppDataBase appDataBase, Retrofit _retrofit, SharedPreferenceUtil _sharedPreference, IsNetworkConnect _isNetworkConnect) {
        disposable = new CompositeDisposable();

        financeHistoryDao = appDataBase.getFinanceDao();
        financeCategoryDao = appDataBase.getFinanceCategoryDao();
        listDao = appDataBase.getListDao();
        financeCategorySyncDao = appDataBase.getFinanceCategorySyncDao();
        financeHistorySyncDao = appDataBase.getFinanceHistorySyncDao();

        network = _retrofit.create(FinanceNetworkInterface.class);
        retrofit = _retrofit;
        sharedPreference = _sharedPreference;
        networkConnect = _isNetworkConnect;
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
        FinanceCategoryApiModel responseModel = new FinanceCategoryApiModel(category);
        Call<FinanceCategoryApiModel> call = network.createCategory(responseModel);
        call.enqueue(new Callback<FinanceCategoryApiModel>() {
            @Override
            public void onResponse(Call<FinanceCategoryApiModel> call, Response<FinanceCategoryApiModel> response) {
                if (response.isSuccessful()) {
                    category.setServerId(response.body().getId());
                    category.setDateMod(response.body().getDateMod());
                    new Thread(() -> financeCategoryDao.update(category)).start();
                }
            }

            @Override
            public void onFailure(Call<FinanceCategoryApiModel> call, Throwable t) {

            }
        });
    }

    public void createNewList(long categoryId, FinanceCategoryModel category) {
        ListModel list = new ListModel(category.getName(), String.valueOf(System.currentTimeMillis()), category.getIconPath(), categoryId);
        new Thread(() -> {
            list.setId(listDao.insert(list));

            createNetworkNewList(list);
        }).start();
    }

    private void createNetworkNewList(ListModel list) {
        ListNetworkInterface listNetwork = retrofit.create(ListNetworkInterface.class);
        ListDataApiModel listData = new ListDataApiModel(list);
        Call<ListDataApiModel> call = listNetwork.createNewList(listData);
        call.enqueue(new Callback<ListDataApiModel>() {
            @Override
            public void onResponse(Call<ListDataApiModel> call, Response<ListDataApiModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    list.setServerId(response.body().getId());
                    list.setDateMod(response.body().getDateMod());
                    new Thread(() -> listDao.update(list)).start();
                }
            }

            @Override
            public void onFailure(Call<ListDataApiModel> call, Throwable t) {

            }
        });
    }

    public void getAllData(int type, String dateStart, String dateEnd, FinanceHistoryCallback callback) {
        disposable.add(financeCategoryDao.getAll(type, dateStart, dateEnd)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .doOnError(throwable -> {
                })
                .subscribe(callback::setAllItems));
        sync();
    }

    private void sync() {
        if (sharedPreference.isLogged() && networkConnect.isInternetAvailable()) {
            new Thread(() -> {
                List<FinanceCategorySync> financeCategorySyncs = financeCategorySyncDao.getAll();
                List<LastSyncApiDataModel> syncData = new ArrayList<>();
                for (FinanceCategorySync item : financeCategorySyncs) {
                    syncData.add(new LastSyncApiDataModel(item.getServerId(), item.getDateMod()));
                }
                Call<List<FinanceCategoryApiModel>> call = network.syncCategory(syncData, sharedPreference.getSharedPreferences().getString(Constants.DEVICE_ID, ""));

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

                call.enqueue(new Callback<List<FinanceCategoryApiModel>>() {
                    @Override
                    public void onResponse(Call<List<FinanceCategoryApiModel>> call, Response<List<FinanceCategoryApiModel>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().size() > 0) {
                            new Thread(() -> {
                                for (FinanceCategoryApiModel responseItem : response.body()) {
                                    financeCategorySyncDao.clearAll();
                                    new Thread(() -> financeCategorySyncDao.insert(new FinanceCategorySync(responseItem))).start();
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
                    }

                    @Override
                    public void onFailure(Call<List<FinanceCategoryApiModel>> call, Throwable t) {

                    }
                });
            }).start();

            syncItems();
        }
    }

    private void syncItems() {
        new Thread(() -> {
            List<FinanceHistoryModel> items = financeHistoryDao.getAll();
            List<FinanceHistorySync> lastSyncData = financeHistorySyncDao.getAll();
            for (FinanceHistoryModel modelItem : items) {
                if (modelItem.getIsDelete() == 1) {
                    Call<ResponseBody> call = network.removeHistoryItem(modelItem.getServerId());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                new Thread(() -> {
                                    financeHistoryDao.delete(modelItem);
                                }).start();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }
            }

            Call<List<FinanceHistoryApiModel>> call = network.syncHistory(lastSyncData, sharedPreference.getSharedPreferences().getString(Constants.DEVICE_ID, ""));
            call.enqueue(new Callback<List<FinanceHistoryApiModel>>() {
                @Override
                public void onResponse(Call<List<FinanceHistoryApiModel>> call, Response<List<FinanceHistoryApiModel>> response) {
                    if (response.isSuccessful()) {
                        new Thread(() -> {
                            financeHistorySyncDao.clear();
                        }).start();

                        if (response.body() != null && response.body().size() > 0) {
                            new Thread(() -> {
                                for (FinanceHistoryApiModel item : response.body()) {
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
                                        financeHistoryDao.insert(new FinanceHistoryModel(item));
                                    }
                                }
                            }).start();
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<FinanceHistoryApiModel>> call, Throwable t) {

                }
            });
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
        Call<FinanceCategoryApiModel> call = network.updateCategory(categoryNetworkModel);
        call.enqueue(new Callback<FinanceCategoryApiModel>() {
            @Override
            public void onResponse(Call<FinanceCategoryApiModel> call, Response<FinanceCategoryApiModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    item.setDateMod(response.body().getDateMod());
                    new Thread(() -> financeCategoryDao.update(item));
                }
            }

            @Override
            public void onFailure(Call<FinanceCategoryApiModel> call, Throwable t) {

            }
        });
    }

    public void remove(FinanceCategoryModel financeCategory) {
        if (networkConnect.isInternetAvailable() && financeCategory.getServerId() > 0) {
            Call<ResponseBody> call = network.removeCategory(financeCategory.getServerId());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        new Thread(() -> financeCategoryDao.delete(financeCategory)).start();
                    } else {
                        financeCategory.setIsDelete(1);
                        financeCategory.setDateMod(String.valueOf(System.currentTimeMillis()));
                        new Thread(() -> financeCategoryDao.update(financeCategory)).start();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    financeCategory.setIsDelete(1);
                    financeCategory.setDateMod(String.valueOf(System.currentTimeMillis()));
                    new Thread(() -> financeCategoryDao.update(financeCategory)).start();
                }
            });
        }
    }

    public void createNewHistoryItem(FinanceHistoryModel item) {
        new Thread(() -> {
            item.setId(financeHistoryDao.insert(item));
            long serverFinanceCategory = financeCategoryDao.getServerId(item.getCategoryId());
            FinanceHistoryApiModel apiModel = new FinanceHistoryApiModel(null, item.getComment(), item.getTotal(), item.getDate(), item.getUserEmail(), item.getType(), serverFinanceCategory, item.getId(), item.getIsDelete() == 1, item.getDateMod());

            Call<FinanceHistoryApiModel> call = network.createItemHistory(apiModel);
            call.enqueue(new Callback<FinanceHistoryApiModel>() {
                @Override
                public void onResponse(Call<FinanceHistoryApiModel> call, Response<FinanceHistoryApiModel> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        item.setDateMod(response.body().getDate_mod());
                        item.setDateModServer(response.body().getDate_mod());
                        new Thread(() -> financeHistoryDao.update(item)).start();
                    }
                }

                @Override
                public void onFailure(Call<FinanceHistoryApiModel> call, Throwable t) {

                }
            });
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
