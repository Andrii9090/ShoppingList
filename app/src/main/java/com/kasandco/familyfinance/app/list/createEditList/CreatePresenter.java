package com.kasandco.familyfinance.app.list.createEditList;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.finance.FinanceRepository;
import com.kasandco.familyfinance.app.finance.models.FinanceCategoryModel;
import com.kasandco.familyfinance.app.list.ListActivityScope;
import com.kasandco.familyfinance.app.list.ListRepository;
import com.kasandco.familyfinance.core.Constants;

import java.util.Locale;

import javax.inject.Inject;

@ListActivityScope
public class CreatePresenter extends BaseCreateEditPresenter<CreateListContract.View> implements CreateListContract.Presenter, FinanceRepository.AllCostCategoryCallback, ListRepository.IconCallback, FinanceRepository.FinanceRepositoryCallback {
    @Inject
    public CreatePresenter() {

    }

    @Override
    public void create() {
        if (validate()) {
            if (financeCategory != -1) {
                if (financeCategory == 0) {
                    financeRepository.createNewCategory(new FinanceCategoryModel(name, pathIcon, Constants.TYPE_COSTS, String.valueOf(System.currentTimeMillis())), this);
                } else {
                    createListItem(financeCategory);
                }
            } else {
                createListItem(0);
            }
        } else {
            view.showToast(R.string.text_name_error);
        }
    }

    @Override
    public void nullingSpinnerPosition() {
        financeCategory = -1;
    }


    @Override
    public void viewReady(CreateListContract.View view) {

    }

    @Override
    public void swipeRefresh() {

    }

    @Override
    public void maxLimit() {

    }
}
