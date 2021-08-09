package com.kasandco.familyfinance.app.item;

import android.os.AsyncTask;

import com.kasandco.familyfinance.core.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ItemPresenter extends BasePresenter<ItemActivity> {
    private List<ItemModel>  itemsActual;
    @Inject
    private ItemDao itemDao;

    @Inject
    public ItemPresenter(){

    }

    @Override
    public void viewReady(ItemActivity view) {
        this.view = view;
        getAllItems();
    }

    private void getAllItems(){
        itemsActual = new ArrayList<>();

        class Async extends AsyncTask<Void, Void, Void>{

            @Override
            protected Void doInBackground(Void... voids) {
                itemsActual.addAll(itemDao.getAllItems());
                itemsActual.addAll(itemDao.getAllNotActive());
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);

            }
        }
    }
}
