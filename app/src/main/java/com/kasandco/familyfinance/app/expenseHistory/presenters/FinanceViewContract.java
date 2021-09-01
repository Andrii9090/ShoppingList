package com.kasandco.familyfinance.app.expenseHistory.presenters;

import com.kasandco.familyfinance.core.BaseContract;

public interface FinanceViewContract extends BaseContract {
    void showCreateCategoryFragment(int type);

    void hideCreateFragment();

    void sendPeriodToFragment(String startDate, String endDate);

    void showDatePickerDialog();

    void setTotal(double totalIncome);

    void getTabPositionSelected();
}
