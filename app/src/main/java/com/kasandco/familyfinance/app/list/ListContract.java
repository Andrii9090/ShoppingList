package com.kasandco.familyfinance.app.list;

import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.familyfinance.core.BasePresenterInterface;

import java.util.List;

public interface ListContract extends BasePresenterInterface {
    void addAdapter(ListRvAdapter adapter);
    void showEmptyText(boolean isShow);
    void showToast(int resource);
    void showCreateFragment();

    void showEditFragment(ListModel listModel);

    void showActivityDetails(ListModel listModel);
}
