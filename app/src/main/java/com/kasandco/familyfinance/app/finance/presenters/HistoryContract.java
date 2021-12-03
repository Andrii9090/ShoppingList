package com.kasandco.familyfinance.app.finance.presenters;

import com.kasandco.familyfinance.app.finance.adapters.FinanceCategoryAdapter;
import com.kasandco.familyfinance.app.finance.models.FinanceCategoryModel;
import com.kasandco.familyfinance.core.BaseContract;

public interface HistoryContract extends BaseContract {
    int getHistoryType();

    void setAdapter(FinanceCategoryAdapter adapter);

    void showEditForm(int type, FinanceCategoryModel financeCategoryModel);

    void showToast(int text_error_set_history_category);

    void showCreateHistoryItemForm(long id, int type);

    void startFinanceDetailActivity(FinanceCategoryModel category);

    void showDialogRemove(int position);

    void reloadTotal();
}
