package com.kasandco.familyfinance.app.list;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.core.BasePresenterInterface;
import com.kasandco.familyfinance.core.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ListPresenter extends BasePresenter<ListActivity> implements ListRepository.ListRepositoryInterface {
    @Inject
    ListRepository repository;

    @Inject
    ListRvAdapter adapter;

    private ListContract view;
    private List<ListModel> listItems;

    @Inject
    public ListPresenter() {
        listItems = new ArrayList<>();
    }

    private void getListItems() {
        view.showLoading();
        repository.getAll(this);
        showEmptyText();
        view.addAdapter(adapter);
    }

    private void showEmptyText() {
        view.showEmptyText(listItems.size() > 0);
    }

    @Override
    public void viewReady(ListActivity view) {
        this.view = (ListContract) view;
        getListItems();
    }

    public void setListItems(List<ListModel> listModels) {
        adapter.updateItems(listModels);
        view.hideLoading();
    }

    public void clickShowCreateFragment() {
        view.showCreateFragment();
    }

    public void selectRemoveList() {
        repository.removeList(adapter.getItems().get(adapter.getPosition()));
        adapter.resetPosition();
    }

    public void selectEditList() {
        view.showEditFragment(adapter.getItems().get(adapter.getPosition()));
    }

    public void selectClearBought() {
        adapter.getItems().get(adapter.getPosition()).setDateMod(String.valueOf(System.currentTimeMillis()));
        repository.clearInactiveItems(adapter.getItems().get(adapter.getPosition()));
    }

    public void selectClearAll() {
        adapter.getItems().get(adapter.getPosition()).setDateMod(String.valueOf(System.currentTimeMillis()));
        repository.clearInactiveItems(adapter.getItems().get(adapter.getPosition()));
        repository.clearActiveItems(adapter.getItems().get(adapter.getPosition()));
    }

    public void clickItem(ListRvAdapter.ViewHolder holder) {
        view.showActivityDetails(adapter.getItems().get(holder.getAbsoluteAdapterPosition()));
    }

    public void swipeRefresh() {
        repository.unsubscribe();
        getListItems();
    }
}
