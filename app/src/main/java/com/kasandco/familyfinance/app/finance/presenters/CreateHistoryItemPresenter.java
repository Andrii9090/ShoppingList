package com.kasandco.familyfinance.app.finance.presenters;

import com.kasandco.familyfinance.app.finance.FinanceRepository;
import com.kasandco.familyfinance.app.finance.models.FinanceHistoryModel;
import com.kasandco.familyfinance.core.BasePresenter;

import java.util.GregorianCalendar;

import javax.inject.Inject;

public class CreateHistoryItemPresenter extends BasePresenter<CreateHistoryItemContract> {
    FinanceRepository repository;

    @Inject
    public CreateHistoryItemPresenter(FinanceRepository repository){
        this.repository = repository;
    }

    @Override
    public void viewReady(CreateHistoryItemContract view) {
        this.view =  view;
    }

    @Override
    public void swipeRefresh() {

    }

    public void createNewItem(int type, long categoryId, long serverCategoryId, String amount, String comment, GregorianCalendar selectedDate) {
        repository.createNewHistoryItem(type, categoryId, serverCategoryId, amount, comment, selectedDate);
        view.close();
    }
}
