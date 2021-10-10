package com.kasandco.familyfinance.app.list;

import android.annotation.SuppressLint;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.item.ItemModel;
import com.kasandco.familyfinance.core.BasePresenter;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@ListActivityScope
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

    @Inject
    public void setIsLogged(SharedPreferenceUtil sharedPreferenceUtil) {
        repository.setIsLogged(sharedPreferenceUtil.getSharedPreferences().getString(TOKEN, null) != null);
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
        this.view = view;
        getListItems();
    }

    public void setListItems(List<ListModel> listModels) {
        listItems = listModels;
        adapter.updateItems(listModels);
        view.hideLoading();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void getAllActiveListItems(List<ItemModel> items) {
        int count = 0;
        StringBuilder text = new StringBuilder();
        text.append(view.getStringResource(R.string.text_header_msg)).append(System.lineSeparator());
        for (ItemModel item : items) {
            count++;
            text.append(String.format("%d. %s" + System.lineSeparator(), count, item.getName()));
        }
        text.append(String.format(view.getStringResource(R.string.text_footer_msg), view.getStringResource(R.string.app_name))).append(System.lineSeparator());
        view.runSendIntent(text.toString());
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
        if (adapter.getItems().get(adapter.getPosition()).getFinanceCategoryId() == 0) {
            view.showToast(R.string.text_error_add_cost);
        } else {
            view.showCreateItemHistoryFragment();
            view.setCategoryId(adapter.getItems().get(adapter.getPosition()).getFinanceCategoryId());
        }
    }

    public void onDetach() {
        repository.unsubscribe();
        view = null;
        adapter = null;
    }

    public void selectShareList() {
        copyActiveListItem();
    }

    private void copyActiveListItem() {
        repository.getAllListActiveItem(listItems.get(adapter.getPosition()).getId(), this);
    }
}
