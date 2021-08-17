package com.kasandco.familyfinance.app.list;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.logging.Handler;


import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ListRepository {
    ListDao listDao;
    ListRepositoryInterface callback;

    private Disposable disposable;

    public ListRepository(ListDao listDao){
        this.listDao = listDao;
    }

    public void create(ListModel listModel){
        new Thread(new Runnable() {
            @Override
            public void run() {
                listDao.insert(listModel);
            }
        }).start();
    }

    public void edit(ListModel listModel){
        new Thread(new Runnable() {
            @Override
            public void run() {
                listDao.update(listModel);
            }
        }).start();
    }

    public void getAll(ListRepositoryInterface callback){
        this.callback = callback;
        disposable = listDao.getAllActiveList().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ListModel>>() {
                    @Override
                    public void accept(List<ListModel> listModels) throws Exception {
                        callback.setListItems(listModels);
                    }
                });
    }

    public void removeList(ListModel listModel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                listDao.delete(listModel);
            }
        }).start();
    }

    public void clearInactiveItems(ListModel listModel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                listDao.clearInactiveItems(listModel.getId());
            }
        }).start();
        deleteInactiveListItems(listModel.getId());
    }

    public void clearActiveItems(ListModel listModel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                listDao.clearActiveItems(listModel.getId());
            }
        }).start();
        deleteActiveListItems(listModel.getId());
    }

    private void deleteActiveListItems(long listId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                listDao.deleteActiveListItem(listId);
            }
        }).start();
    }
    private void deleteInactiveListItems(long listId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                listDao.deleteActiveListItem(listId);
            }
        }).start();
    }

    public void unsubscribe() {
        disposable.dispose();
    }

    public interface ListRepositoryInterface {
        void setListItems(List<ListModel> listModel);
    }
}
