package com.kasandco.familyfinance.app.expenseHistory.presenters;

import com.kasandco.familyfinance.app.expenseHistory.adapters.FinanceCategoryAdapter;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryModel;
import com.kasandco.familyfinance.core.BaseContract;

public interface HistoryContract extends BaseContract {
    int getHistoryType();

    void setAdapter(FinanceCategoryAdapter adapter);

    void showEditForm(int type, FinanceCategoryModel financeCategoryModel);

    void unblockButtons();

    void showToast(int text_error_set_history_category);

    void showCreateHistoryItemForm(long id, int type);
}
