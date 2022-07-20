package com.kasandco.shoplist.app.list.createEditList;

import com.kasandco.shoplist.app.list.ListModel;

public interface EditListContract {
    interface View extends CreateEditListBaseView{
        void setIconPosition(int iconPosition);

        void setName(String name);
    }

    interface BasePresenter extends CreateEditListBasePresenter {
        void setEditItem(ListModel item);
    }
}
