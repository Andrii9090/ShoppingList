package com.kasandco.familyfinance.app.list.CreateList;

import com.kasandco.familyfinance.app.list.ListModel;
import com.kasandco.familyfinance.core.BasePresenterInterface;

public interface CreateListContract extends BasePresenterInterface {
    void showToast(int resource);
    void setEditData(ListModel listModel);
    void close();
}
