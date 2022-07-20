package com.kasandco.shoplist.app.list.createEditList;

public interface CreateEditListBasePresenter {
    void viewReady(CreateEditListBaseView view);
    void clickClose();
    void clickCreateBtn();
    void setData(String name, String iconPath);
    void destroyView();
}

