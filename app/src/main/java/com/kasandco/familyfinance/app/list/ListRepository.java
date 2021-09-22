package com.kasandco.familyfinance.app.list;

import android.os.Handler;
import android.os.Looper;


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
        new Thread(() -> listDao.insert(listModel)).start();
    }

    public void edit(ListModel listModel){
        new Thread(() -> listDao.update(listModel)).start();
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
        new Thread(() -> listDao.addFinanceCategoryId(id, categoryId)).start();
    }

    public void removeList(ListModel listModel) {
        new Thread(() -> listDao.delete(listModel)).start();
    }

    public void clearInactiveItems(ListModel listModel) {
        new Thread(() -> listDao.clearInactiveItems(listModel.getId())).start();
        deleteInactiveListItems(listModel.getId());
    }

    public void clearActiveItems(ListModel listModel) {
        new Thread(() -> listDao.clearActiveItems(listModel.getId())).start();
        deleteActiveListItems(listModel.getId());
    }

    private void deleteActiveListItems(long listId){
        new Thread(() -> listDao.deleteActiveListItem(listId)).start();
    }
    private void deleteInactiveListItems(long listId){
        new Thread(() -> listDao.deleteInactiveListItem(listId)).start();
    }

    public void unsubscribe() {
        disposable.dispose();
    }

    public void getAllIcons(IconCallback callback){
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(()->{
            List<IconModel> icons  = iconDao.getAllIcon();
            handler.post(()->{
                if(icons.size()>0){
                    callback.setIcons(icons);
                }else {
                    callback.setIcons(new ArrayList<>());
                }
            });
        }).start();
    }

    public void getAllListActiveItem(long listId, ListRepositoryInterface callback) {
        Handler handler = new Handler();
        new Thread(() -> {
            List<ItemModel> items = itemDao.getActiveItems(listId);
            handler.post(()->{
                callback.getAllActiveListItems(items);
            });
        }).start();
    }

    public interface ListRepositoryInterface {
        void setListItems(List<ListModel> listModel);
        void getAllActiveListItems(List<ItemModel> items);
    }

    public interface IconCallback {
        void setIcons(List<IconModel> iconModels);
    }

}
