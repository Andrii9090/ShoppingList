package com.kasandco.familyfinance.app.expenseHistory.models;

import androidx.room.Embedded;

public class FinanceCategoryWithTotal {
    @Embedded
    FinanceCategoryModel category;

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

    double total;
}
