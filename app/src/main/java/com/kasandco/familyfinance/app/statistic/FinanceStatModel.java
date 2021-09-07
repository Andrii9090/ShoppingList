package com.kasandco.familyfinance.app.statistic;

import androidx.room.Ignore;

public class FinanceStatModel {
    private String name;
    private double total;
    @Ignore
    private float percent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent /100;
    }
}
