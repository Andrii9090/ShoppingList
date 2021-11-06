package com.kasandco.familyfinance.network.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

public class LastSyncApiDataModel {
    @SerializedName("item")
    private long id;
    private String date_mod;

    public LastSyncApiDataModel(long _serverId, String _last_date_mod) {
        id = _serverId;
        date_mod = _last_date_mod;
    }

    public long getId() {
        return id;
    }

    public String getDate_mod() {
        return date_mod;
    }


}
