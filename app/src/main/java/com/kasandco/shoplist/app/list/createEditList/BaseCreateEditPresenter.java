package com.kasandco.shoplist.app.list.createEditList;

import com.kasandco.shoplist.core.icon.AdapterIcon;
import com.kasandco.shoplist.core.icon.IconModel;
import com.kasandco.shoplist.app.list.ListModel;
import com.kasandco.shoplist.app.list.ListRepository;
import com.kasandco.shoplist.core.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public abstract class BaseCreateEditPresenter<T extends CreateEditListBaseView> extends BasePresenter<T> implements CreateEditListBasePresenter, ListRepository.IconCallback{
    @Inject
    ListRepository listRepository;

    List<IconModel> icons;

    @Inject
    AdapterIcon adapterIcon;

    protected List<String> financeCategoryNames;
    protected long financeCategory;
    protected String name, pathIcon;
    protected CreateEditListBaseView view;

    public BaseCreateEditPresenter() {
        financeCategoryNames = new ArrayList<>();
        icons = new ArrayList<>();
        financeCategory = -2;
    }

    @Override
    public void viewReady(CreateEditListBaseView view) {
        this.view = view;
        adapterIcon.setListener((AdapterIcon.OnClickIconListener) view);
        listRepository.getAllIcons(this);
    }

    public abstract void create();

    protected void createListItem(long financeCategory) {
        ListModel listModel = new ListModel(name, String.valueOf(System.currentTimeMillis()), pathIcon, financeCategory);
        listRepository.create(listModel);
        view.close();
    }

    protected boolean validate() {
        if (name != null && name.length() > 2 && pathIcon!=null) {
            return true;
        }
        return false;
    }

    public void clickCreateBtn() {
        view.getInputData();
    }

    public void clickClose() {
        adapterIcon.setDefaultBackground();
        view.clearViewData();
    }

    public void setData(String _name, String _iconPath) {
        name = _name;
        pathIcon = _iconPath;
        create();
    }

    @Override
    public void setIcons(List<IconModel> iconModels) {
        icons.clear();
        icons.addAll(iconModels);
        adapterIcon.setItems(iconModels);
        view.setRecyclerViewAdapter(adapterIcon);
    }

    @Override
    public void destroyView() {
        adapterIcon.setDefaultBackground();
        view = null;
    }
}


