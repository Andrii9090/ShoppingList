package com.kasandco.familyfinance.core;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public abstract class BaseModel {
    @PrimaryKey(autoGenerate = true)
    protected long id;
    @ColumnInfo(name = "date_mod")
    protected String dateMod;
    @ColumnInfo(name = "date_mod_server", defaultValue = "0")
    protected String dateModServer;
    @ColumnInfo(name = "is_delete")
    protected int isDelete;
    @ColumnInfo(name = "server_id")
    protected long serverId;

    public BaseModel(){}

    public BaseModel(String _dateModServer, String _dateMod, int _isDelete, long _id, long  _serverId){
        id=_id;
        serverId = _serverId;
        dateMod = _dateMod;
        dateModServer = _dateModServer;
        isDelete = _isDelete;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public String getDateModServer() {
        return dateModServer;
    }

    public void setDateModServer(String dateModServer) {
        this.dateModServer = dateModServer;
    }
}
