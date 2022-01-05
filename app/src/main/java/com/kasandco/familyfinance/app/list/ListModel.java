package com.kasandco.familyfinance.app.list;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.gson.annotations.SerializedName;
import com.kasandco.familyfinance.core.BaseModel;
import com.kasandco.familyfinance.network.model.ListApiModel;

import java.io.Serializable;

@Entity(tableName = "list")
public class ListModel extends BaseModel implements Serializable {

    private String name;

    @ColumnInfo(name = "is_private", defaultValue = "1")
    private int isPrivate;

    @ColumnInfo(name = "icon_id", defaultValue = "0")
    private String icon;

    @ColumnInfo(name = "is_cost", defaultValue = "0")
    private int isCost;

    @ColumnInfo(name = "cost_category_id", defaultValue = "0")
    private Long financeCategoryId;

    public ListModel() {

    }

    @Ignore
    public ListModel(String name, String dateMod, String dateModServer, String iconPath, long financeCategoryId) {
        this.icon = iconPath;
        this.dateMod = dateMod;
        this.name = name;
        this.dateModServer = dateModServer;
        this.financeCategoryId = financeCategoryId;
        isPrivate = 1;
    }

    @Ignore
    public ListModel(String name, String dateMod, String iconPath, long financeCategoryId) {
        this.icon = iconPath;
        this.dateMod = dateMod;
        this.name = name;
        this.financeCategoryId = financeCategoryId;
        isPrivate = 1;
    }

    public ListModel(ListApiModel listData) {
        id = listData.getLocalId();
        serverId = listData.getId();
        name = listData.getName();
        dateMod = listData.getDateMod();
        dateModServer = listData.getDateMod();
        isDelete = listData.isDelete() ? 1 : 0;
        icon = listData.getIcon();
        financeCategoryId = listData.getFinanceCategoryId();
        isPrivate = listData.isPrivate() ? 1 : 0;
        isOwner = listData.isOwner() ? 1 : 0;
    }

    public ListModel(ListModel editItem) {
        icon = editItem.getIcon();
        name = editItem.getName();
        id = editItem.getId();
        financeCategoryId = editItem.getFinanceCategoryId();
        serverId = editItem.getServerId();
        isPrivate = editItem.getIsPrivate();
        isDelete = editItem.getIsDelete();
        isCost = editItem.getIsCost();
        dateMod = editItem.getDateMod();
        dateModServer = editItem.getDateModServer();
        isOwner = editItem.getIsOwner();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIsCost() {
        return isCost;
    }

    public void setIsCost(int isCost) {
        this.isCost = isCost;
    }

    public Long getFinanceCategoryId() {
        return financeCategoryId;
    }

    public void setFinanceCategoryId(Long financeCategoryId) {
        this.financeCategoryId = financeCategoryId;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public String getDateMod() {
        return dateMod;
    }

    public void setDateMod(String dateMod) {
        this.dateMod = dateMod;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(int isPrivate) {
        this.isPrivate = isPrivate;
    }
}
