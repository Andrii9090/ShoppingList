package com.kasandco.familyfinance.app.expenseHistory.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "finance_category", indices = {@Index(value = {"name"},
        unique = true)})
public class FinanceCategoryModel {
    @PrimaryKey(autoGenerate = true) private long id;
    private String name;
    @ColumnInfo(defaultValue = "1") private int type;
    @ColumnInfo(name = "icon_path") private String iconPath;
    @ColumnInfo(name = "server_id") private long serverId;
    @ColumnInfo(name = "is_delete", defaultValue = "0") private int isDelete;
    @ColumnInfo(name = "date_mod") private String dateMod;
    @Ignore private String total;

    public FinanceCategoryModel(String name, String iconPath, int type, String dateMod){
        this.name = name;
        this.iconPath = iconPath;
        this.type = type;
        this.dateMod = dateMod;
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
}
