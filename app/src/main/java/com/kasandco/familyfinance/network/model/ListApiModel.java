package com.kasandco.familyfinance.network.model;

import com.google.gson.annotations.SerializedName;
import com.kasandco.familyfinance.app.list.ListModel;

import java.util.Objects;

public class ListApiModel {
    private long id;

    private String name;

    @SerializedName("icon_path")
    private String icon;

    @SerializedName("finance_category")
    private long financeCategoryId;

    @SerializedName("shared_token")
    private String token;

    @SerializedName("local_id")
    private long localId;

    @SerializedName("date_mod")
    private String dateMod;

    @SerializedName("is_delete")
    private boolean isDelete;

    public ListApiModel(ListModel listModel){
        id = listModel.getServerId();
        name = listModel.getName();
        icon = listModel.getIcon();
        token = listModel.getListCode();
        localId = listModel.getId();
        if(listModel.getDateMod().length()==10) {
            dateMod = listModel.getDateMod();
        }else {
            dateMod = listModel.getDateMod().substring(0,10);
        }
        financeCategoryId = listModel.getFinanceCategoryId();
        isDelete = listModel.getIsDelete() == 1;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Long getFinanceCategoryId() {
        return financeCategoryId;
    }

    public void setFinanceCategoryId(Long financeCategoryId) {
        this.financeCategoryId = financeCategoryId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public String getDateMod() {
        return dateMod;
    }

    public void setDateMod(String dateMod) {
        this.dateMod = dateMod;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    @Override
    public boolean equals(Object o) {
        ListApiModel that = (ListApiModel) o;
        return id == that.id || localId == that.localId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, icon, financeCategoryId, token, localId, dateMod, isDelete);
    }
}
