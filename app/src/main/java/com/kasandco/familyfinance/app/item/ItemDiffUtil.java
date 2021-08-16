package com.kasandco.familyfinance.app.item;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;
import java.util.Objects;

public class ItemDiffUtil extends DiffUtil.Callback {
    List<ItemModel> oldData;
    List<ItemModel> newData;

    public ItemDiffUtil(List<ItemModel> oldData, List<ItemModel> newData){
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
        return oldItem.getId()==newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        ItemModel oldItem = oldData.get(oldItemPosition);
        ItemModel newItem = newData.get(newItemPosition);
        return oldItem.getQuantity().equals(newItem.getQuantity()) && oldItem.getName().equals(newItem.getName()) && oldItem.getImagePath().equals(Objects.requireNonNull(newItem.getImagePath()));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        ItemModel oldItem = oldData.get(oldItemPosition);
        ItemModel newItem = newData.get(newItemPosition);
        if(oldItem.getImagePath()==null && newItem.getImagePath()==null){
            return true;
        }
        if (oldItem.getImagePath() != null && newItem.getImagePath()!= null && oldItem.getImagePath().equals(newItem.getImagePath())){
            return true;
        }
        else return false;
    }
}
