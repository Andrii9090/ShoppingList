package com.kasandco.familyfinance.app.list.createEditList;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.expenseHistory.FinanceRepository;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryModel;
import com.kasandco.familyfinance.app.list.ListActivityScope;
import com.kasandco.familyfinance.app.list.ListRepository;
import com.kasandco.familyfinance.core.Constants;

import javax.inject.Inject;

@ListActivityScope
public class FragmentCreatePresenter extends BaseCreateEditPresenter<CreateListContract.View> implements CreateListContract.Presenter, ListRepository.FinanceCategoryListener, FinanceRepository.AllCostCategoryCallback, ListRepository.IconCallback, FinanceRepository.FinanceRepositoryCallback {
    @Inject
    public FragmentCreatePresenter() {

    }

    @Override
    public void create() {
        if (validate()) {
            if(financeCategory!=-1){
                if(financeCategory==0){
                    financeRepository.createNewCategory(new FinanceCategoryModel(name, pathIcon, Constants.TYPE_COSTS, String.valueOf(System.currentTimeMillis())), this, true);
                }else {
                    createListItem(financeCategory);
                }
            }else{
                createListItem(0);
            }
        } else {
            view.showToast(R.string.text_name_error);
        }
    }
}
