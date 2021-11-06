package com.kasandco.familyfinance.app.finance.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.kasandco.familyfinance.network.model.FinanceCategoryApiModel;

@Entity(tableName = "finance_category_sync")
public class FinanceCategorySync {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "server_id")
    private long serverId;
    @ColumnInfo(name = "date_mod")
    private String dateMod;

    public FinanceCategorySync(){}

    public FinanceCategorySync(FinanceCategoryApiModel response){
        serverId = response.getId();
        dateMod = response.getDateMod();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
