package com.kasandco.familyfinance.app.list.createEditList;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryModel;
import com.kasandco.familyfinance.app.icon.IconModel;
import com.kasandco.familyfinance.app.list.ListActivityScope;
import com.kasandco.familyfinance.app.list.ListModel;

import java.util.List;

import javax.inject.Inject;

@ListActivityScope
public class EditListPresenter extends BaseCreateEditPresenter<EditListContract.View> implements EditListContract.BasePresenter {
    private ListModel editItem;
    private int iconPosition, positionSpinner;
    protected EditListContract.View view;

    @Inject
    public EditListPresenter() {
        positionSpinner = -1;
        iconPosition = -1;
    }

    @Override
    public void viewReady(CreateEditListBaseView view) {
        super.viewReady(view);
        this.view = (EditListContract.View) view;
    }

    @Override
    public void create() {
        boolean isEdit = false;
        if (!editItem.getName().equals(name)) {
            editItem.setName(name);
            isEdit = true;
        }
        if (editItem.getIcon() != null && !editItem.getIcon().equals(pathIcon) || editItem.getIcon() == null && pathIcon != null && !pathIcon.isEmpty()) {
            editItem.setIcon(pathIcon);
            isEdit = true;
        }
        if (editItem.getFinanceCategoryId() != financeCategory) {
            editItem.setFinanceCategoryId(financeCategory);
            isEdit = true;
        }
        if (isEdit) {
            editItem.setDateMod(String.valueOf(System.currentTimeMillis()));
            listRepository.edit(editItem);
        }
        view.close();
    }

    @Override
    public void setEditItem(ListModel item) {
        editItem = item;
        view.setName(editItem.getName());
    }

    @Override
    public void setAllCostCategory(List<FinanceCategoryModel> categoryModels) {
        super.setAllCostCategory(categoryModels);
        financeCategoryNames.set(0,view.getStringResource(R.string.text_no_selected_finance_category));
        setEditDataSpinner();
    }

    @Override
    public void setIcons(List<IconModel> iconModels) {
        super.setIcons(iconModels);
        setEditDataIcon();
    }

    private void setEditDataSpinner() {
        if (editItem.getFinanceCategoryId() != 0) {
            for (FinanceCategoryModel category : financeCategoryModelList) {
                if (category.getId() == editItem.getFinanceCategoryId()) {
                    positionSpinner = financeCategoryModelList.indexOf(category);
                    break;
                }
            }
            if (positionSpinner >= 0) {
                positionSpinner += 2;
            }
        }
        view.setSpinnerPosition(positionSpinner);
    }

    private void setEditDataIcon(){
        if (editItem.getIcon() != null && !editItem.getIcon().isEmpty()) {
            adapterIcon.setIcon(editItem.getIcon());
            for (IconModel model : icons) {
                if (model.path.equals(editItem.getIcon())) {
                    iconPosition = icons.indexOf(model);
                    adapterIcon.setIcon(editItem.getIcon());
                    break;
                }
            }
        }
        view.setIconPosition(iconPosition);
    }
}
