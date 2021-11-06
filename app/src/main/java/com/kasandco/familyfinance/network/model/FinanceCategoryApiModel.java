package com.kasandco.familyfinance.network.model;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;
import com.kasandco.familyfinance.app.finance.models.FinanceCategoryModel;

public class FinanceCategoryApiModel {
    private long id;
    @SerializedName("local_id")
    private long localId;

    private String name;
    private int type;
    @SerializedName("icon_path")
    private String iconPath;
    @SerializedName("is_delete")
    private boolean isDelete;
    @SerializedName("date_mod")
    private String dateMod;
    private double total;

    public FinanceCategoryApiModel(){}
    public FinanceCategoryApiModel(FinanceCategoryModel category){
        id = category.getServerId();
        localId = category.getId();
        name = category.getName();
        type = category.getType();
        iconPath = category.getIconPath();
        isDelete = category.getIsDelete() == 1;
        total = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public String getDateMod() {
        return dateMod;
    }

    public void setDateMod(String dateMod) {
        this.dateMod = dateMod;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

}
