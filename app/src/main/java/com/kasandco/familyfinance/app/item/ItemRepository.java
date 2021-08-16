package com.kasandco.familyfinance.app.item;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.kasandco.familyfinance.App;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ItemRepository {
    @Inject
    ItemDao itemDao;

    ItemPresenter presenter;

    private Flowable<List<ItemModel>> allItems;

    @Inject
    public ItemRepository() {
        App.getAppComponent().plus(new DataModule()).inject(this);
    }

    public void create(String[] arrayText) {
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                itemDao.insert(new ItemModel(arrayText[0], ""));
            }
        }).start();

    }

    public void setPresenter(ItemPresenter presenter) {
        this.presenter = presenter;
    }

    public void remove(ItemModel item) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                itemDao.delete(item);
            }
        }).start();
    }

    public void edit(ItemModel item) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                itemDao.update(item);
            }
        }).start();
    }

    public void getAll() {
        itemDao.getAllActiveItems()
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                itemDao.saveImagePath(imagePath, id);
            }
        }).start();
    }
}
