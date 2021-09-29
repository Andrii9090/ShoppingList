package com.kasandco.familyfinance.app.list.createEditList;

public interface CreateEditListBasePresenter {
    void viewReady(CreateEditListBaseView view);
    void clickClose();
    void clickCreateBtn();
    void setData(String name, String iconPath);
    void selectedSpinner(int i);
    void destroyView();
    void nullingSpinnerPosition();
}

