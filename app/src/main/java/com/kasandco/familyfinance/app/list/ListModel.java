package com.kasandco.familyfinance.app.list;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "list")
public class ListModel implements Serializable {
    public ListModel(){

    }

    @PrimaryKey(autoGenerate = true) private long id;

    private String name;

    @ColumnInfo(name = "is_owner", defaultValue = "1")
    private int isOwner;
    @ColumnInfo(name = "icon_id", defaultValue = "0")
    private String icon;

    @ColumnInfo(name = "is_cost", defaultValue = "0")
    private int isCost;

    @ColumnInfo(name = "cost_category_id", defaultValue = "0")
    private long statisticCategoryId;

    @ColumnInfo(name = "list_code", defaultValue = "0")
    private int listCode;

    @ColumnInfo(name = "server_id", defaultValue = "0")
    private int serverId;

    @ColumnInfo(name = "date_mod")
    private String dateMod;

    @ColumnInfo(name = "is_delete", defaultValue = "0")
    private int isDelete;

    @ColumnInfo(name = "quantity_active", defaultValue = "0")
    private int quantityActive;

    @ColumnInfo(name = "quantity_inactive", defaultValue = "0")
    private int quantityInactive;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Ignore
    public ListModel(String name, String dateMod, String iconPath, long statId){
        this.icon =iconPath;
        this.dateMod = dateMod;
        this.name = name;
        this.statisticCategoryId = statId;
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

    public long getStatisticCategoryId() {
        return statisticCategoryId;
    }

    public void setStatisticCategoryId(int statisticCategoryId) {
        this.statisticCategoryId = statisticCategoryId;
    }

    public int getListCode() {
        return listCode;
    }

    public void setListCode(int listCode) {
        this.listCode = listCode;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
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
        cloneItem.statisticCategoryId = this.statisticCategoryId;
        cloneItem.listCode = this.listCode;
        cloneItem.serverId = this.serverId;
        cloneItem.dateMod = this.dateMod;
        cloneItem.isDelete = this.isDelete;
        cloneItem.quantityActive = this.quantityActive;
        cloneItem.quantityInactive = this.quantityInactive;
        return cloneItem;
    }
}
