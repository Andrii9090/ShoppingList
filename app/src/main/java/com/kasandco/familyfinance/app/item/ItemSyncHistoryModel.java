package com.kasandco.familyfinance.app.item;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "item_sync_history")
public class ItemSyncHistoryModel {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @SerializedName("server_id")
    @ColumnInfo(name = "server_id")
    private long serverId;
    @SerializedName("server_list_id")
    @ColumnInfo(name = "server_list_id")
    private long serverListId;
    @SerializedName("date_mod")
    @ColumnInfo(name = "date_mod")
    private String dateMod;

    public ItemSyncHistoryModel(long _id, long _serverId, String _dateMod) {
        id = _id;
        serverId = _serverId;
        dateMod = _dateMod;
    }

    public ItemSyncHistoryModel() {
    }

    public ItemSyncHistoryModel(long _serverId, String _dateMod, long _serverListId) {
        serverId = _serverId;
        dateMod = _dateMod;
        serverListId = _serverListId;
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

    public long getServerListId() {
        return serverListId;
    }

    public void setServerListId(long serverListId) {
        this.serverListId = serverListId;
    }
}
