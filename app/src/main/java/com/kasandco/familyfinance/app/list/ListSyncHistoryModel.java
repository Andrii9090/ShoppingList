package com.kasandco.familyfinance.app.list;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.kasandco.familyfinance.network.model.ListDataApiModel;

@Entity(tableName = "list_sync_history")
public class ListSyncHistoryModel {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "server_id")
    private long serverId;

    @ColumnInfo(name = "date_mod")
    private String dateMod;


    public ListSyncHistoryModel(){}

    public ListSyncHistoryModel(ListDataApiModel networkListData){
        serverId = networkListData.getId();
        dateMod = networkListData.getDateMod();

    }

    public void setId(long id) {
        this.id = id;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public void setDateMod(String dateMod) {
        this.dateMod = dateMod;
    }

    public long getId() {
        return id;
    }

    public long getServerId() {
        return serverId;
    }

    public String getDateMod() {
        return dateMod;
    }

}
