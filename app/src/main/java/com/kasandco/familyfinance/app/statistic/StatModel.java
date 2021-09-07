package com.kasandco.familyfinance.app.statistic;

import java.util.ArrayList;
import java.util.List;

public class StatModel {
    private String name;
    private float total;
    private float percent;
    private List<FinanceStatModel> items;

    public StatModel() {

    }

    public StatModel(String _name, float _total, float _percent) {
        name = _name;
        total = _total;
        percent = _percent;
        items = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent / 100;
    }

    public List<FinanceStatModel> getItems() {
        return items;
    }

    public void setItem(FinanceStatModel item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        this.items.add(item);
    }
}
