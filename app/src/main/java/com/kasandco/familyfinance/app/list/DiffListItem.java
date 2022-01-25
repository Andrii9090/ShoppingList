package com.kasandco.familyfinance.app.list;

import androidx.recyclerview.widget.DiffUtil;

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
        if(newItems!=null) {
            return newItems.size();
        }else{
            return 0;
        }
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
        return !oldItem.getDateMod().equals(newItem.getDateMod()) || oldItem.getDateModServer()!= null && newItem.getDateModServer()!=null && !oldItem.getDateModServer().equals(newItem.getDateModServer());
    }
}
