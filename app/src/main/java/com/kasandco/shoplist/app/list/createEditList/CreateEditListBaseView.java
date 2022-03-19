package com.kasandco.shoplist.app.list.createEditList;

import com.kasandco.shoplist.core.icon.AdapterIcon;
import com.kasandco.shoplist.core.BaseContract;

import java.util.List;

public interface CreateEditListBaseView extends BaseContract {
    void showToast(int resource);

    void close();

    void setDataToSelection(List<String> names);

    void setRecyclerViewAdapter(AdapterIcon adapterIcon);

    void getInputData();

    String getStringResource(int text_empty_statistic_category);

    void clearViewData();
}
