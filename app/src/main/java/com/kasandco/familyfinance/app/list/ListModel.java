package com.kasandco.familyfinance.app.list;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.gson.annotations.SerializedName;
import com.kasandco.familyfinance.core.BaseModel;
import com.kasandco.familyfinance.network.model.ListDataApiModel;

import java.io.Serializable;

@Entity(tableName = "list")
public class ListModel extends BaseModel implements Serializable {

    private String name;

    @ColumnInfo(name = "is_owner", defaultValue = "1")
    private int isOwner;

    @ColumnInfo(name = "icon_id", defaultValue = "0")
    private String icon;

    @ColumnInfo(name = "is_cost", defaultValue = "0")
    private int isCost;

    @ColumnInfo(name = "cost_category_id", defaultValue = "0")
    private Long financeCategoryId;

    @ColumnInfo(name = "list_code", defaultValue = "")
    private String listCode;

    @SerializedName("active_quantity")
    @ColumnInfo(name = "quantity_active", defaultValue = "0")
    private int quantityActive;

    @SerializedName("no_active_quantity")
    @ColumnInfo(name = "quantity_inactive", defaultValue = "0")
    private int quantityInactive;

    public ListModel(){

    }

    @Ignore
    public ListModel(String name, String dateMod, String iconPath, long financeCategoryId){
        this.icon =iconPath;
        this.dateMod = dateMod;
        this.name = name;
        this.financeCategoryId = financeCategoryId;
    }

    public ListModel(ListDataApiModel listData){
        id = listData.getLocalId();
        serverId = listData.getId();
        name = listData.getName();
        dateMod = listData.getDateMod();
        dateModServer = listData.getDateMod();
        quantityActive = listData.getQuantityActive();
        quantityInactive = listData.getQuantityInactive();
        isDelete = listData.isDelete()?1:0;
        icon = listData.getIcon();
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

    public int getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(int isOwner) {
        this.isOwner = isOwner;
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

    public String getListCode() {
        return listCode;
    }

    public void setListCode(String listCode) {
        this.listCode = listCode;
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

    public int getQuantityActive() {
        return quantityActive;
    }

    public void setQuantityActive(int quantityActive) {
        this.quantityActive = quantityActive;
    }

    public int getQuantityInactive() {
        return quantityInactive;
    }

    public void setQuantityInactive(int quantityInactive) {
        this.quantityInactive = quantityInactive;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public ListModel clone() {
        ListModel cloneItem = new ListModel();

        cloneItem.id = this.id;
        cloneItem.name = this.name;
        cloneItem.isOwner = this.isOwner;
        cloneItem.icon = this.icon;
        cloneItem.isCost = this.isCost;
        cloneItem.financeCategoryId = this.financeCategoryId;
        cloneItem.listCode = this.listCode;
        cloneItem.serverId = this.serverId;
        cloneItem.dateMod = this.dateMod;
        cloneItem.isDelete = this.isDelete;
        cloneItem.quantityActive = this.quantityActive;
        cloneItem.quantityInactive = this.quantityInactive;
        return cloneItem;
    }


}
