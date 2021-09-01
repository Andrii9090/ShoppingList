package com.kasandco.familyfinance.app.expenseHistory;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryDao;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryModel;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryWithTotal;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceDao;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceModel;

import java.util.ArrayList;
import java.util.List;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FinanceRepository {
    FinanceCategoryDao financeCategoryDao;

    FinanceDao financeHistoryDao;
    private CompositeDisposable disposable;


    public FinanceRepository(FinanceCategoryDao financeCategoryDao, FinanceDao financeHistoryDao){
        this.financeHistoryDao = financeHistoryDao;
        this.financeCategoryDao = financeCategoryDao;
        disposable = new CompositeDisposable();
    }

    public void createNewCategory(FinanceCategoryModel category, FinanceRepositoryCallback callback, boolean checked) {
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    long id = financeCategoryDao.insert(category);
                    @Override
                    public void run() {
                        try{
                            callback.added(id);
                        }catch (SQLiteConstraintException e){
                            callback.notUnique();
                        }
                    }
                });
            }
        }).start();
    }

    public void getAllData(int type, String dateStart, String dateEnd, FinanceHistoryCallback callback) {
        disposable.add(financeCategoryDao.getAll(type, dateStart, dateEnd)
                .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.newThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                    }
                })
                .subscribe(new Consumer<List<FinanceCategoryWithTotal>>() {
                    @Override
                    public void accept(List<FinanceCategoryWithTotal> financeCategoryModels) throws Throwable {
                        callback.setAllItems(financeCategoryModels);
                    }
                }));
    }

    public void getAllCostCategory(AllCostCategoryCallback callback){
        financeCategoryDao.getAllCostCategory()
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        callback.setAllCostCategory(new ArrayList<>());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<List<FinanceCategoryModel>>() {
                    @Override
                    public void accept(List<FinanceCategoryModel> financeCategoryModels) throws Throwable {
                        callback.setAllCostCategory(financeCategoryModels);
                    }
                });
    }

    public void clearDisposable(){
        disposable.clear();
    }

    public void edit(FinanceCategoryModel editItem, FinanceRepositoryCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                financeCategoryDao.update(editItem);
                callback.added(0);
            }
        }).start();
    }

    public void remove(FinanceCategoryModel financeCategoryModel) {
        new  Thread(new Runnable() {
            @Override
            public void run() {
                financeCategoryDao.delete(financeCategoryModel);
            }
        }).start();
    }

    public void createNewHistoryItem(FinanceModel item) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                financeHistoryDao.insert(item);
            }
        }).start();
    }

    public void getTotalToPeriod(String startDate, String endDate, FinanceTotalResult callback) {
        financeHistoryDao.getTotalToPeriod(1, startDate, endDate)
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        callback.setTotal(1,0.0);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<Double>() {
                    @Override
                    public void accept(Double aDouble) throws Throwable {
                        callback.setTotal(1, aDouble);
                    }
                });
        financeHistoryDao.getTotalToPeriod(2, startDate, endDate)
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        callback.setTotal(2,0.0);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<Double>() {
                    @Override
                    public void accept(Double aDouble) throws Throwable {
                        callback.setTotal(2, aDouble);
                    }
                });
    }

    public interface FinanceRepositoryCallback{
        void notUnique();

        void added(long id);

    }

    public interface FinanceHistoryCallback{
        void setAllItems(List<FinanceCategoryWithTotal> historyList);
    }

    public interface FinanceTotalResult{
        void setTotal(int type, double res);
    }

    public interface AllCostCategoryCallback{
        void setAllCostCategory(List<FinanceCategoryModel> categoryModels);
    }
}
