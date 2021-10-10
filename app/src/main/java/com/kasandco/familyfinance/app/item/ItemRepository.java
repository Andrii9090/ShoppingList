package com.kasandco.familyfinance.app.item;

import com.kasandco.familyfinance.App;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

@ItemActivityScope
public class ItemRepository {
    @Inject
    ItemDao itemDao;
    ItemPresenter presenter;
    //@TODO пересмотреть почему тут презентер
    long listId;
    private Disposable disposable;

    @Inject
    public ItemRepository() {
        App.getAppComponent().plus(new DataModule()).inject(this);
    }

    public void create(String[] arrayText) {
        new Thread(() -> {
            itemDao.insert(new ItemModel(arrayText[0], listId));
            plusActiveItem(listId);
        }).start();
    }

    public void plusActiveItem(long listId) {
        itemDao.plusActiveItemsInList(listId);
    }
    public void minusActiveItem(long listId) {
        itemDao.minusActiveItemsInList(listId);
    }
    public void plusInactiveItem(long listId) {
        itemDao.plusInactiveItemsInList(listId);
    }
    public void minusInactiveItem(long listId) {
        itemDao.minusInactiveItemsInList(listId);
    }

    public void setPresenter(ItemPresenter presenter) {
        this.presenter = presenter;
    }

    public void remove(ItemModel item) {
        new Thread(() -> {
            itemDao.delete(item);
            if (item.getStatus() == 1) {
                itemDao.minusActiveItemsInList(listId);
            } else {
                itemDao.minusInactiveItemsInList(listId);
            }
        }).start();
    }

    public void edit(ItemModel item) {
        new Thread(() -> itemDao.update(item)).start();
    }

    public void getAll(long listId) {
        this.listId = listId;
        disposable = itemDao.getAllItems(listId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ItemModel>>() {
                    @Override
                    public void accept(List<ItemModel> list) throws Throwable {
                        presenter.setItems(list);
                    }
                });
    }


    public void saveImagePath(String imagePath, long id) {
        new Thread(() -> itemDao.saveImagePath(imagePath, id)).start();
    }

    public void unSubscribe() {
        disposable.dispose();
    }

    public void changeStatus(ItemModel item) {
        new Thread(() -> {
            if(item.getStatus()==0){
                plusInactiveItem(listId);
                minusActiveItem(listId);
            }else {
                plusActiveItem(listId);
                minusInactiveItem(listId);
            }
            itemDao.update(item);
        }).start();
    }
}
