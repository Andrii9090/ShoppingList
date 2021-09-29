package com.kasandco.familyfinance.app.finance.presenters;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.finance.FinanceRepository;
import com.kasandco.familyfinance.app.finance.adapters.FinanceCategoryAdapter;
import com.kasandco.familyfinance.app.finance.models.FinanceCategoryWithTotal;
import com.kasandco.familyfinance.core.BasePresenter;

import java.util.List;

public class PresenterFinanceHistory extends BasePresenter<HistoryContract> implements FinanceRepository.FinanceHistoryCallback {
    private int type;
    private FinanceCategoryAdapter adapter;
    private String startDate;
    private String endDate;

    private FinanceRepository repository;

    public PresenterFinanceHistory(FinanceRepository repository, FinanceCategoryAdapter adapter) {
        this.repository = repository;
        this.adapter = adapter;
    }

    @Override
    public void viewReady(HistoryContract view) {
        this.view = view;
        type = view.getHistoryType();
        getData();
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
    public void setAllItems(List<FinanceCategoryWithTotal> historyList) {
        if (historyList.size() > 0) {
            adapter.setItems(historyList);
            view.setAdapter(adapter);
        }
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
                repository.remove(adapter.getItems().get(position).getCategory());
                break;
        }
        adapter.nullingPosition();
    }

    public void clickToCategoryItem() {
        view.unblockButtons();
    }

    public void clickToCreateNewHistoryItem() {
        if (adapter.getCurrentPosition() != -1) {
            view.showCreateHistoryItemForm(adapter.getItems().get(adapter.getCurrentPosition()).getCategory().getId(), type);
        } else {
            view.showToast(R.string.text_error_set_history_category);
        }
    }

    public void periodSet(String startDate, String endDate) {
        if (this.startDate != null) {
            setNewPeriod(startDate, endDate);
        } else {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    public void destroy() {
        repository.clearDisposable();
    }
}
