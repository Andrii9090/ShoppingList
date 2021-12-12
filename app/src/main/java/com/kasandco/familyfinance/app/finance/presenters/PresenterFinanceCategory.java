package com.kasandco.familyfinance.app.finance.presenters;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.finance.FinanceRepository;
import com.kasandco.familyfinance.app.finance.adapters.FinanceCategoryAdapter;
import com.kasandco.familyfinance.app.finance.models.FinanceCategoryWithTotal;
import com.kasandco.familyfinance.core.BasePresenter;

import java.util.List;

public class PresenterFinanceCategory extends BasePresenter<HistoryContract> implements FinanceRepository.FinanceHistoryCallback {
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
    public void viewReady(HistoryContract view) {
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

    public void contextMenuClick(int itemId) {
        int position = adapter.getCurrentPosition();
        switch (itemId) {
            case R.id.context_finance_category_edit:
                view.showEditForm(type, adapter.getItems().get(position).getCategory());
                break;
            case R.id.context_finance_category_detail:
                view.startFinanceDetailActivity(adapter.getItems().get(position).getCategory());
                break;
            case R.id.context_finance_category_remove:
                view.showDialogRemove(position);
                break;
        }
        adapter.nullingPosition();
    }

    public void clickToCreateNewHistoryItem() {
        if (adapter.getCurrentPosition() != -1) {
            view.showCreateHistoryItemForm(adapter.getItems().get(adapter.getCurrentPosition()).getCategory().getId(), type);
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
}
