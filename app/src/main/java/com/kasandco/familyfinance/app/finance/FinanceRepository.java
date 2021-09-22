package com.kasandco.familyfinance.app.finance;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Handler;
import android.os.Looper;

import com.kasandco.familyfinance.app.finance.models.FinanceCategoryDao;
import com.kasandco.familyfinance.app.finance.models.FinanceCategoryModel;
import com.kasandco.familyfinance.app.finance.models.FinanceCategoryWithTotal;
import com.kasandco.familyfinance.app.finance.models.FinanceDao;
import com.kasandco.familyfinance.app.finance.models.FinanceModel;
import com.kasandco.familyfinance.app.list.ListDao;
import com.kasandco.familyfinance.app.list.ListModel;

import java.util.ArrayList;
import java.util.List;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FinanceRepository {
    private ListDao listDao;
    FinanceCategoryDao financeCategoryDao;

    FinanceDao financeHistoryDao;
    private CompositeDisposable disposable;


    public FinanceRepository(FinanceCategoryDao financeCategoryDao, FinanceDao financeHistoryDao, ListDao listDao){
        this.financeHistoryDao = financeHistoryDao;
        this.financeCategoryDao = financeCategoryDao;
        this.listDao = listDao;
        disposable = new CompositeDisposable();
    }

    public void createNewCategory(FinanceCategoryModel category, FinanceRepositoryCallback callback, boolean checked) {
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            try {
                long id = financeCategoryDao.insert(category);
                handler.post(() -> callback.added(id));
            }catch (SQLiteConstraintException e) {
                handler.post(callback::notUnique);
            }
        }).start();
    }

    public void createNewList(long categoryId, FinanceCategoryModel category){
        ListModel list = new ListModel(category.getName(), String.valueOf(System.currentTimeMillis()), category.getIconPath(), categoryId);
        new Thread(() -> listDao.insert(list)).start();
    }

    public void getAllData(int type, String dateStart, String dateEnd, FinanceHistoryCallback callback) {
        disposable.add(financeCategoryDao.getAll(type, dateStart, dateEnd)
                .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.newThread())
                .doOnError(throwable -> {
                })
                .subscribe(callback::setAllItems));
    }

    public void getAllCostCategory(AllCostCategoryCallback callback){
        financeCategoryDao.getAllCostCategory()
                .doOnError(throwable -> callback.setAllCostCategory(new ArrayList<>()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(callback::setAllCostCategory);
    }

    public void clearDisposable(){
        disposable.clear();
    }

    public void edit(FinanceCategoryModel editItem, FinanceRepositoryCallback callback) {
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            financeCategoryDao.update(editItem);
            handler.post(()->{
                callback.added(0);
            });
        }).start();
    }

    public void remove(FinanceCategoryModel financeCategoryModel) {
        new  Thread(() -> financeCategoryDao.delete(financeCategoryModel)).start();
    }

    public void createNewHistoryItem(FinanceModel item) {
        new Thread(() -> financeHistoryDao.insert(item)).start();
    }

    public void getTotalToPeriod(String startDate, String endDate, FinanceTotalResult callback) {
        financeHistoryDao.getTotalToPeriod(1, startDate, endDate)
                .doOnError(throwable -> callback.setTotal(1,0.0))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(aDouble -> callback.setTotal(1, aDouble));
        financeHistoryDao.getTotalToPeriod(2, startDate, endDate)
                .doOnError(throwable -> callback.setTotal(2,0.0))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(aDouble -> callback.setTotal(2, aDouble));
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
