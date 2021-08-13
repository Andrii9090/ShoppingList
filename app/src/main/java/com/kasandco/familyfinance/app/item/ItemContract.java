package com.kasandco.familyfinance.app.item;

import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.familyfinance.core.BasePresenterInterface;

import java.util.List;

public interface ItemContract extends BasePresenterInterface {
    void startAdapter(RecyclerView.Adapter<?> adapter);

    void showEditForm(ItemModel item);
}
