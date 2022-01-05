package com.kasandco.familyfinance.app.item;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class ItemDiffUtil extends DiffUtil.Callback {
    List<ItemModel> oldData;
    List<ItemModel> newData;

    public ItemDiffUtil(List<ItemModel> oldData, List<ItemModel> newData) {
        this.oldData = oldData;
        this.newData = newData;
    }

    @Override
    public int getOldListSize() {
        return oldData.size();
    }

    @Override
    public int getNewListSize() {
        return newData.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        ItemModel oldItem = oldData.get(oldItemPosition);
        ItemModel newItem = newData.get(newItemPosition);
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        ItemModel oldItem = oldData.get(oldItemPosition);
        ItemModel newItem = newData.get(newItemPosition);
        return !oldItem.getDateMod().equals(newItem.getDateMod());
    }
}
