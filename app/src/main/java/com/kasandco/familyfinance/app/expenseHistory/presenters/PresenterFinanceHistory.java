package com.kasandco.familyfinance.app.expenseHistory.presenters;

import android.annotation.SuppressLint;
import android.util.Log;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.expenseHistory.FinanceRepository;
import com.kasandco.familyfinance.app.expenseHistory.adapters.FinanceCategoryAdapter;
import com.kasandco.familyfinance.app.expenseHistory.core.FinanceActivityScope;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryModel;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryWithTotal;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceModel;
import com.kasandco.familyfinance.core.BasePresenter;

import java.util.GregorianCalendar;
import java.util.List;

@FinanceActivityScope
public class PresenterFinanceHistory extends BasePresenter<HistoryContract> implements FinanceRepository.FinanceHistoryCallback {
    private int type;
    private FinanceCategoryAdapter adapter;
    private String startDate;
    private String endDate;

    private FinanceRepository repository;

    public PresenterFinanceHistory(FinanceRepository repository){
        this.repository = repository;
    }

    @Override
    public void viewReady(HistoryContract view) {
        this.view = view;
        type = view.getHistoryType();
        getData();
    }

    private void getData() {
        repository.getAllData(type, startDate, endDate,this);
    }

    public void setNewPeriod(String startDate, String endDate){
        this.startDate = startDate;
        this.endDate = endDate;
        getData();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void setAllItems(List<FinanceCategoryWithTotal> historyList) {
        adapter = new FinanceCategoryAdapter();
        if(historyList.size()>0) {
            adapter.setItems(historyList);
            view.setAdapter(adapter);
        }
    }

    public void contextMenuClick(int itemId) {
        int position = adapter.getCurrentPosition();
        switch (itemId){
            case R.id.context_finance_category_edit:
                view.showEditForm(type, adapter.getItems().get(position).getCategory());
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
        if(adapter.getCurrentPosition()!=-1){
            view.showCreateHistoryItemForm(adapter.getItems().get(adapter.getCurrentPosition()).getCategory().getId(), type);
        }else {
            view.showToast(R.string.text_error_set_history_category);
        }
    }

    public void periodSet(String startDate, String endDate) {
        if(this.startDate!=null){
            setNewPeriod(startDate, endDate);
        }else {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    public void destroy() {
        repository.clearDisposable();
    }
}
