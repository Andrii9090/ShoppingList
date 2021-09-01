package com.kasandco.familyfinance.app.list;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.expenseHistory.fragments.FragmentCreateItemHistory;
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

    public void selectAddCost() {
        if(adapter.getItems().get(adapter.getPosition()).getFinanceCategoryId()==0){
            view.showToast(R.string.text_error_add_cost);
        }else {
            view.showCreateItemHistoryFragment();
            view.setCategoryId(adapter.getItems().get(adapter.getPosition()).getFinanceCategoryId());
        }
    }
}
