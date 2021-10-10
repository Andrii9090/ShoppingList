package com.kasandco.familyfinance.app.list.createEditList;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.finance.FinanceRepository;
import com.kasandco.familyfinance.app.finance.models.FinanceCategoryModel;
import com.kasandco.familyfinance.core.icon.AdapterIcon;
import com.kasandco.familyfinance.core.icon.IconModel;
import com.kasandco.familyfinance.app.list.ListModel;
import com.kasandco.familyfinance.app.list.ListRepository;
import com.kasandco.familyfinance.core.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public abstract class BaseCreateEditPresenter<T extends CreateEditListBaseView> extends BasePresenter<T> implements CreateEditListBasePresenter, FinanceRepository.AllCostCategoryCallback, ListRepository.IconCallback, FinanceRepository.FinanceRepositoryCallback {
    @Inject
    ListRepository listRepository;

    @Inject
    FinanceRepository financeRepository;

    List<IconModel> icons;

    @Inject
    AdapterIcon adapterIcon;

    protected List<FinanceCategoryModel> financeCategoryModelList;
    protected List<String> financeCategoryNames;
    protected long statId;
    protected long financeCategory;
    protected String name, pathIcon, textSpinnerSelected;
    protected CreateEditListBaseView view;

    public BaseCreateEditPresenter() {
        financeCategoryNames = new ArrayList<>();
        icons = new ArrayList<>();
        financeCategory = -2;
    }

    @Override
    public void viewReady(CreateEditListBaseView view) {
        this.view = view;
        financeCategoryModelList = new ArrayList<>();
        adapterIcon.setListener((AdapterIcon.OnClickIconListener) view);
        loadFinanceCategory();
        listRepository.getAllIcons(this);
    }

    protected void loadFinanceCategory() {
        financeRepository.getAllCostCategory(this);
    }

    public abstract void create();

    protected void createListItem(long financeCategory) {
        ListModel listModel = new ListModel(name, String.valueOf(System.currentTimeMillis()), pathIcon, financeCategory);
        listRepository.create(listModel);
        view.close();
    }

    protected boolean validate() {
        if (name == null && name.length() < 2) {
            return false;
        } else return !textSpinnerSelected.isEmpty();
    }

    public void clickCreateBtn() {
        view.getInputData();
    }

    public void clickClose() {
        adapterIcon.setDefaultBackground();
        nullingSpinnerPosition();
        view.clearViewData();
    }

    public void setData(String _name, String _iconPath) {
        name = _name;
        pathIcon = _iconPath;
        create();
    }

    public void selectedSpinner(int position) {
        if (position == 0) {
            textSpinnerSelected = "nothing";
            financeCategory = -1;
        } else if (position == 1) {
            financeCategory = 0;
        } else {
            textSpinnerSelected = financeCategoryNames.get(position);
            getFinanceCategoryId();
        }
    }

    private void getFinanceCategoryId() {
        for (FinanceCategoryModel financeCategory : financeCategoryModelList) {
            if (financeCategory.getName().equals(textSpinnerSelected)) {
                this.financeCategory = financeCategory.getId();
                break;
            }
        }
    }

    @Override
    public void setAllCostCategory(List<FinanceCategoryModel> categoryModels) {
        if (view != null) {
            financeCategoryModelList.clear();
            financeCategoryNames.clear();
            financeCategoryModelList.addAll(categoryModels);
            financeCategoryNames.add(view.getStringResource(R.string.text_empty_statistic_category));
            financeCategoryNames.add(view.getStringResource(R.string.create_category));
            for (FinanceCategoryModel item : financeCategoryModelList) {
                financeCategoryNames.add(item.getName());
            }
            view.setDataToSelection(financeCategoryNames);
        }
    }

    @Override
    public void setIcons(List<IconModel> iconModels) {
        icons.clear();
        icons.addAll(iconModels);
        adapterIcon.setItems(iconModels);
        view.setRecyclerViewAdapter(adapterIcon);
    }

    @Override
    public void notUnique() {
    }

    @Override
    public void added(long financeCategoryId) {
        createListItem(financeCategoryId);
    }

    @Override
    public void destroyView() {
        adapterIcon.setDefaultBackground();
        view = null;
        financeRepository.clearDisposable();
    }
}


