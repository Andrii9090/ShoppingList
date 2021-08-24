package com.kasandco.familyfinance.app.expenseHistory;

import android.database.sqlite.SQLiteConstraintException;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryDao;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryModel;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryWithTotal;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceDao;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceModel;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FinanceRepository {
    FinanceCategoryDao financeCategoryDao;

    FinanceDao financeHistoryDao;
    private Disposable disposable;


    public FinanceRepository(FinanceCategoryDao financeCategoryDao, FinanceDao financeHistoryDao){
        this.financeHistoryDao = financeHistoryDao;
        this.financeCategoryDao = financeCategoryDao;
    }

    public void createNewCategory(FinanceCategoryModel category, FinanceRepositoryCallback callback, boolean checked) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    financeCategoryDao.insert(category);
                    callback.added();
                }catch (SQLiteConstraintException e){
                    callback.notUnique();
                }
            }
        }).start();
        if (checked) {
            financeCategoryDao.getLastRow()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Consumer<FinanceCategoryModel>() {
                        @Override
                        public void accept(FinanceCategoryModel financeCategoryModel) throws Exception {
                            callback.setLastCategory(category);
                        }
                    }).isDisposed();
        }

    }

    public void getAllData(int type, FinanceHistoryCallback callback) {
        disposable = financeCategoryDao.getAll(type, "0", "1629916272418")
                .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.newThread())
                .subscribe(new io.reactivex.rxjava3.functions.Consumer<List<FinanceCategoryWithTotal>>() {
                    @Override
                    public void accept(List<FinanceCategoryWithTotal> financeCategoryModels) throws Throwable {
                        callback.setAllItems(financeCategoryModels);
                    }
                });
    }

    public void edit(FinanceCategoryModel editItem, FinanceRepositoryCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                financeCategoryDao.update(editItem);
                callback.added();
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

    public interface FinanceRepositoryCallback{
        void setLastCategory(FinanceCategoryModel category);

        void notUnique();

        void added();

    }

    public interface FinanceHistoryCallback{
        void setAllItems(List<FinanceCategoryWithTotal> historyList);
    }
}
