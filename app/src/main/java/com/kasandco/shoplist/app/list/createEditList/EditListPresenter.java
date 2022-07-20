package com.kasandco.shoplist.app.list.createEditList;

import com.kasandco.shoplist.core.icon.IconModel;
import com.kasandco.shoplist.app.list.ListActivityScope;
import com.kasandco.shoplist.app.list.ListModel;

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
        ListModel model = new ListModel(editItem);
        if (!editItem.getName().equals(name)) {
            model.setName(name);
        }
        if (editItem.getIcon() != null && !editItem.getIcon().equals(pathIcon) || editItem.getIcon() == null && pathIcon != null) {
            model.setIcon(pathIcon);
        }
        if (editItem.getFinanceCategoryId()!=null && editItem.getFinanceCategoryId() != financeCategory) {
            model.setFinanceCategoryId(financeCategory);
        }
        model.setDateMod(String.valueOf(System.currentTimeMillis()));
        listRepository.update(model);
        view.close();
    }

    @Override
    public void setEditItem(ListModel item) {
        editItem = item;
        view.setName(editItem.getName());
    }

    @Override
    public void setIcons(List<IconModel> iconModels) {
        super.setIcons(iconModels);
        setEditDataIcon();
    }

    private void setEditDataIcon() {
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

    @Override
    public void viewReady(EditListContract.View view) {

    }

    @Override
    public void swipeRefresh() {

    }
}
