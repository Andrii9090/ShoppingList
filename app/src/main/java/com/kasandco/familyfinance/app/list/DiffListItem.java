package com.kasandco.familyfinance.app.list;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.kasandco.familyfinance.app.item.ItemModel;

import java.util.List;

public class DiffListItem extends DiffUtil.Callback {
    private List<ListModel> oldItems, newItems;

    public DiffListItem(List<ListModel> oldItems, List<ListModel> newItems) {
        this.newItems = newItems;
        this.oldItems = oldItems;
    }

    @Override
    public int getOldListSize() {
        return oldItems.size();
    }

    @Override
    public int getNewListSize() {
        return newItems.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        ListModel oldItem = oldItems.get(oldItemPosition);
        ListModel newItem = newItems.get(newItemPosition);
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        ListModel oldItem = oldItems.get(oldItemPosition);
        ListModel newItem = newItems.get(newItemPosition);
        return oldItem.getName()!=null && newItem.getName()!=null && oldItem.getName().equals(newItem.getName()) && oldItem.getDateMod().equals(newItem.getDateMod());
    }
}
