package com.kasandco.familyfinance.app.finance.presenters;

import android.util.Log;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.finance.FinanceRepository;
import com.kasandco.familyfinance.app.finance.adapters.FinanceCategoryAdapter;
import com.kasandco.familyfinance.app.finance.models.FinanceCategoryWithTotal;
import com.kasandco.familyfinance.core.BasePresenter;

import java.util.List;

public class PresenterFinanceCategory extends BasePresenter<FinanceCategoryContract> implements FinanceRepository.FinanceHistoryCallback {
    private int type;
    private FinanceCategoryAdapter adapter;
    private String startDate;
    private String endDate;

    private FinanceRepository repository;

    public PresenterFinanceCategory(FinanceRepository repository, FinanceCategoryAdapter adapter) {
        this.repository = repository;
        this.adapter = adapter;
    }

    @Override
    public void viewReady(FinanceCategoryContract view) {
        this.view = view;
        type = this.view.getHistoryType();
        setNewPeriod(startDate, endDate);
    }

    private void getData() {
        repository.getAllData(type, startDate, endDate, this);
    }

    public void setNewPeriod(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        getData();
    }

    @Override
    public void setAllItems(List<FinanceCategoryWithTotal> categoryList) {
        adapter.setItems(categoryList);
        view.setAdapter(adapter);
    }

    @Override
    public void noPerm() {
        view.showToast(R.string.text_no_permissions);
    }

    @Override
    public void error() {
        view.showToast(R.string.error_load_data);
    }

    @Override
    public void noInternet() {
        view.showToast(R.string.internet_connection_error);
    }

    public void clickToCreateNewHistoryItem() {
        if (adapter.getCurrentPosition() != -1) {
            view.showCreateHistoryItemForm(adapter.getItems().get(adapter.getCurrentPosition()).getCategory().getId(), adapter.getItems().get(adapter.getCurrentPosition()).getCategory().getServerId(), type);
        } else {
            view.showToast(R.string.text_error_set_history_category);
        }
    }

    public void periodSet(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        if (type != 0) {
            setNewPeriod(startDate, endDate);
        }
    }

    public void destroy() {
        repository.clearDisposable();
    }

    @Override
    public void swipeRefresh() {

    }

    public void selectRemoveList(int position) {
        repository.remove(adapter.getItems().get(position).getCategory());
        view.reloadTotal();
    }

    public void clickEdit() {
        view.showEditForm(type, adapter.getItems().get(adapter.getCurrentPosition()).getCategory());
        adapter.nullingPosition();
    }

    public void clickDetail() {
        view.startFinanceDetailActivity(adapter.getItems().get(adapter.getCurrentPosition()).getCategory());
        adapter.nullingPosition();
    }

    public void clickCategoryRemove() {
        view.showDialogRemove(adapter.getCurrentPosition());
        adapter.nullingPosition();
    }

    public void clickPrivate() {
        repository.setPrivate(adapter.getItems().get(adapter.getCurrentPosition()));
    }

    public void clickReloadData() {
        view.animateBtnReload(true);
        setNewPeriod(startDate, endDate);
    }
}
