package com.kasandco.familyfinance.app.list;

import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.familyfinance.core.BasePresenterInterface;

import java.util.List;

public interface ListContract extends BasePresenterInterface {
    void addAdapter();
    void showEmptyText(boolean isShow);
    void showToast(int resource);
    void updateAdapter(int position, boolean isEdit);
    void editListItem(ListModel listItem);
    void removeItemUpdate(int position);
}
