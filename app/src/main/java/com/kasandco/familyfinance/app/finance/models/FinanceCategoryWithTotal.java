package com.kasandco.familyfinance.app.finance.models;

import androidx.room.Embedded;
import androidx.room.Ignore;

public class FinanceCategoryWithTotal {
    @Embedded
    FinanceCategoryModel category;
    double total;
    @Ignore
    private boolean isSelected;

    public FinanceCategoryModel getCategory() {
        return category;
    }

    public void setCategory(FinanceCategoryModel category) {
        this.category = category;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
