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
public class CreatePresenter extends BaseCreateEditPresenter<CreateListContract.View> implements CreateListContract.Presenter, FinanceRepository.AllCostCategoryCallback, ListRepository.IconCallback, FinanceRepository.FinanceRepositoryCallback, ListRepository.ListResponseListener {
    @Inject
    public CreatePresenter() {

    }

    @Override
    public void create() {
        if (name.toLowerCase(Locale.ROOT).startsWith("token")) {
            String[] path = name.split(" ");
            listRepository.subscribeToList(path[1], this);
        } else {
            if (validate()) {
                if (financeCategory != -1) {
                    if (financeCategory == 0) {
                        financeRepository.createNewCategory(new FinanceCategoryModel(name, pathIcon, Constants.TYPE_COSTS, String.valueOf(System.currentTimeMillis())), this, true);
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
    }

    @Override
    public void nullingSpinnerPosition() {
        financeCategory = -1;
    }

    @Override
    public void closeCreateForm(){
        view.close();
    }

    @Override
    public void noSubscribed() {
        view.showToast(R.string.error_no_added_subscribe_list);
    }
}
