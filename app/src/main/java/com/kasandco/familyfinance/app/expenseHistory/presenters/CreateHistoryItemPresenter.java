package com.kasandco.familyfinance.app.expenseHistory.presenters;

import com.kasandco.familyfinance.app.expenseHistory.FinanceRepository;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceModel;
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

    public void createNewItem(int type, long categoryId, String amount, String comment, GregorianCalendar selectedDate) {
        FinanceModel item = new FinanceModel(String.valueOf(selectedDate.getTime().getTime()), categoryId, Double.parseDouble(amount), comment, type, String.valueOf(System.currentTimeMillis()));
        repository.createNewHistoryItem(item);
        view.close();
    }
}
