package com.kasandco.familyfinance.app.expenseHistory.presenters;

import com.kasandco.familyfinance.app.expenseHistory.FinanceRepository;
import com.kasandco.familyfinance.core.BasePresenter;

public class FinanceActivityPresenter extends BasePresenter<FinanceViewContract> {
    private FinanceRepository repository;

    public FinanceActivityPresenter(FinanceRepository repository){
        this.repository = repository;
    }

    @Override
    public void viewReady(FinanceViewContract view) {
        this.view = view;
    }

    public void clickBtnNewCategory(int type) {
        view.showCreateCategoryFragment(type);
    }

    public void clickCloseCreateFragment() {
        view.hideCreateFragment();
    }
}
