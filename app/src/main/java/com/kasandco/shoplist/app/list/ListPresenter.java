package com.kasandco.shoplist.app.list;

import android.annotation.SuppressLint;

import com.kasandco.shoplist.R;
import com.kasandco.shoplist.app.item.ItemModel;
import com.kasandco.shoplist.core.BasePresenter;

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

    private void getListItems() {
        view.showLoading();
        repository.getAll(this);
        view.addAdapter(adapter);
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

    @Override
    public void noConnectionToInternet() {
        view.showToastNoInternet();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void getAllActiveListItems(List<ItemModel> items) {
        if (items.size() > 0) {
            int count = 0;
            StringBuilder text = new StringBuilder();
            text.append(view.getStringResource(R.string.text_header_msg)).append(System.lineSeparator());
            for (ItemModel item : items) {
                count++;
                text.append(String.format("%d. %s" + System.lineSeparator(), count, item.getName()));
            }
            text.append(String.format(view.getStringResource(R.string.text_footer_msg), view.getStringResource(R.string.app_name))).append(System.lineSeparator());
            view.runSendIntent(text.toString());
        } else {
            view.showToast(R.string.not_active_items);
        }
    }

    @Override
    public void noPermit() {
        view.showToast(R.string.text_no_permissions);
    }

    @Override
    public void error() {
        view.showToast(R.string.error_load_data);
    }

    public void clickShowCreateFragment() {
        if(!repository.isLogged() && adapter.getItemCount()>=MAX_QUANTITY_WITHOUT_REG){
            view.showToast(R.string.text_error_max_quantity);
        }else {
            view.showCreateFragment();
        }
    }

    public void selectRemoveList() {
        repository.removeList(adapter.getItems().get(adapter.getPosition()));
        adapter.resetPosition();
    }

    public void selectEditList() {
        view.showEditFragment(adapter.getItems().get(adapter.getPosition()));
    }

    public void selectClearBought() {
        repository.removeInactiveItems(adapter.getItems().get(adapter.getPosition()));
    }

    public void selectClearAll() {
        repository.removeAllItems(adapter.getItems().get(adapter.getPosition()));
    }

    public void clickItem(ListRvAdapter.ViewHolder holder) {
        view.showActivityDetails(adapter.getItems().get(holder.getAbsoluteAdapterPosition()));
    }

    @Override
    public void swipeRefresh() {
        getListItems();
    }

    public void onDetach() {
        repository.unsubscribe();
        view = null;
        adapter = null;
    }

    public void selectSendListToMessage() {
        copyActiveListItem();
    }

    private void copyActiveListItem() {
        repository.getAllListActiveItem(listItems.get(adapter.getPosition()).getId(), this);
    }

    public void selectPrivateList() {
        repository.setPrivate(adapter.getItems().get(adapter.getPosition()));
    }
}
