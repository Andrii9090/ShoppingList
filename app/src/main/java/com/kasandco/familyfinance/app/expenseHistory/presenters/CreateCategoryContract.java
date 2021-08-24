package com.kasandco.familyfinance.app.expenseHistory.presenters;

import com.kasandco.familyfinance.core.BaseContract;

public interface CreateCategoryContract extends BaseContract {
    void getInputData();

    void showToast(int resource);

    void close();
}
