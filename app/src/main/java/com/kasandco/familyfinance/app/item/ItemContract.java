package com.kasandco.familyfinance.app.item;

import com.kasandco.familyfinance.core.BasePresenterInterface;

import java.util.List;

public interface ItemContract extends BasePresenterInterface {
    void startAdapter(List<ItemModel> items);
}
