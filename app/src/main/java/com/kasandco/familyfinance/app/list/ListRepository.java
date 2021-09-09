package com.kasandco.familyfinance.app.list;

import android.os.Handler;


import com.kasandco.familyfinance.core.icon.IconDao;
import com.kasandco.familyfinance.core.icon.IconModel;
import com.kasandco.familyfinance.app.item.ItemDao;
import com.kasandco.familyfinance.app.item.ItemModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ListRepository {
    ListDao listDao;
    ItemDao itemDao;
    ListRepositoryInterface callback;

    IconDao iconDao;

    private Disposable disposable;

    public ListRepository(ListDao _listDao, IconDao _iconDao, ItemDao _itemDao){
        listDao = _listDao;
        iconDao=_iconDao;
        itemDao=_itemDao;
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
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        callback.setListItems(null);
                    }
                })
                .subscribe(new Consumer<List<ListModel>>() {
                    @Override
                    public void accept(List<ListModel> listModels) throws Exception {
                        callback.setListItems(listModels);
                    }
                });
    }

    public void addFinanceCategoryId(long id, long categoryId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                listDao.addFinanceCategoryId(id, categoryId);
            }
        }).start();
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
                listDao.deleteInactiveListItem(listId);
            }
        }).start();
    }

    public void unsubscribe() {
        disposable.dispose();
    }

    public void getLastId(FinanceCategoryListener callback) {
        listDao.getLastId()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Throwable {
                        callback.setLastId(aLong);
                    }
                });
    }

    public void createFinanceHistory(long financeCategoryId) {

    }

    public void getAllIcons(IconCallback callback){
        iconDao.getAllIcon().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        callback.setIcons(new ArrayList<>());
                    }
                })
                .subscribe(new Consumer<List<IconModel>>() {
                    @Override
                    public void accept(List<IconModel> iconModels) throws Throwable {
                        callback.setIcons(iconModels);
                    }
                });
    }

    public void getAllListActiveItem(long listId, ListRepositoryInterface callback) {
        Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ItemModel> items = itemDao.getActiveItems(listId);
                handler.post(()->{
                    callback.getAllActiveListItems(items);
                });
            }
        }).start();
    }

    public interface ListRepositoryInterface {
        void setListItems(List<ListModel> listModel);
        void getAllActiveListItems(List<ItemModel> items);
    }

    public interface IconCallback {
        void setIcons(List<IconModel> iconModels);
    }

    public interface FinanceCategoryListener{
        void setLastId(long id);
    }

}
