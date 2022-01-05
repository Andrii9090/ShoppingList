package com.kasandco.familyfinance.app.finance.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import com.google.firebase.FirebaseApp;
import com.kasandco.familyfinance.core.BaseModel;
import com.kasandco.familyfinance.network.model.FinanceCategoryApiModel;

@Entity(tableName = "finance_category", indices = {@Index(value = {"name"},
        unique = true)})
public class FinanceCategoryModel extends BaseModel {

    private String name;
    @ColumnInfo(name = "is_private", defaultValue = "1")
    private int isPrivate;
    @ColumnInfo(defaultValue = "1")
    private int type;
    @ColumnInfo(name = "icon_path")
    private String iconPath;
    @Ignore
    private String total;
    private String date;

    @Ignore
    public FinanceCategoryModel(String name, String iconPath, int type, String dateMod) {
        super();
        this.name = name;
        this.iconPath = iconPath;
        this.type = type;
        this.dateMod = dateMod;
        isPrivate = 1;
    }

    public FinanceCategoryModel(String name, String iconPath, int type, String dateMod, String date) {
        super();
        this.name = name;
        this.iconPath = iconPath;
        this.type = type;
        this.dateMod = dateMod;
        this.date = date;
        isPrivate = 1;
    }

    public FinanceCategoryModel(FinanceCategoryApiModel responseModel) {
        super();
        name = responseModel.getName();
        iconPath = responseModel.getIconPath();
        type = responseModel.getType();
        dateMod = responseModel.getDateMod();
        dateModServer = responseModel.getDateMod();
        isDelete = responseModel.getIsDelete() ? 1 : 0;
        serverId = responseModel.getId();
        id = responseModel.getLocalId();
        isPrivate = responseModel.isPrivate()?1:0;
        isOwner = responseModel.isOwner()?1:0;
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

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public String getDateMod() {
        return dateMod;
    }

    public void setDateMod(String dateMod) {
        this.dateMod = dateMod;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public int getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(int isPrivate) {
        this.isPrivate = isPrivate;
    }
}
