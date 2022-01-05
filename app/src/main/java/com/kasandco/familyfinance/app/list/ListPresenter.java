package com.kasandco.familyfinance.app.list;

import android.annotation.SuppressLint;

import com.google.firebase.messaging.FirebaseMessaging;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.item.ItemModel;
import com.kasandco.familyfinance.core.BasePresenter;

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

    private void showEmptyText() {
        if(listItems!=null)
        view.showEmptyText(listItems.size() > 0);
    }

    @Override
    public void viewReady(ListActivity view) {
        this.view = view;
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        repository.saveFMCToken(task.getResult());
                    }
                });
        getListItems();
    }

    public void setListItems(List<ListModel> listModels) {
        listItems = listModels;
        showEmptyText();
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

    public void selectAddCost() {
        if (adapter.getItems().get(adapter.getPosition()).getFinanceCategoryId() == null || adapter.getItems().get(adapter.getPosition()).getFinanceCategoryId() == 0) {
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
