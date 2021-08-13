package com.kasandco.familyfinance.app.item;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.familyfinance.core.BasePresenter;

import java.util.List;

import javax.inject.Inject;

public class ItemPresenter extends BasePresenter<ItemContract> {

    ItemDao itemDao;

    ItemAdapter adapter;

    ItemRepository repository;

    public ItemPresenter(ItemRepository repository, ItemDao dao, ItemAdapter adapter){
        this.repository = repository;
        this.itemDao = dao;
        this.adapter = adapter;
        repository.setPresenter(this);
    }

    @Override
    public void viewReady(ItemContract view) {
        this.view = view;
        repository.getAll();
        view.startAdapter(adapter);
    }

    public void removeItem() {
        repository.remove(adapter.items.get(adapter.getPosition()));
    }

    public void clickEdit() {
        view.showEditForm(adapter.items.get(adapter.getPosition()));
    }

    public void setItems(List<ItemModel> list) {
        adapter.updateList(list);
    }

    public void updateData(ItemModel itemModel) {
        adapter.setNewItem(itemModel);
    }
}
