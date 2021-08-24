package com.kasandco.familyfinance.app.list.CreateList;

import com.kasandco.familyfinance.app.list.ListModel;
import com.kasandco.familyfinance.core.BaseContract;

public interface CreateListContract extends BaseContract {
    void showToast(int resource);
    void setEditData(ListModel listModel);
    void close();
}
