package com.kasandco.familyfinance.app.expenseHistory;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryModel;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryWithTotal;

import java.util.List;

public class DiffUtilFinanceAdapter extends DiffUtil.Callback{
    private List<FinanceCategoryWithTotal> oldItems, newItems;

    public DiffUtilFinanceAdapter(List<FinanceCategoryWithTotal> oldItems, List<FinanceCategoryWithTotal> newItems){
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
        return oldItems.get(oldItemPosition).getCategory().getId() == newItems.get(newItemPosition).getCategory().getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        FinanceCategoryWithTotal oldItem = oldItems.get(oldItemPosition);
        FinanceCategoryWithTotal newItem = newItems.get(newItemPosition);

        return oldItem.getCategory().getName().equals(newItem.getCategory().getName()) && !oldItem.getCategory().getDateMod().equals(newItem.getCategory().getDateMod());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        FinanceCategoryWithTotal oldItem = oldItems.get(oldItemPosition);
        FinanceCategoryWithTotal newItem = newItems.get(newItemPosition);
        return !oldItem.getCategory().getIconPath().equals(newItem.getCategory().getIconPath());
    }
}
