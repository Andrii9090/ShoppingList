package com.kasandco.familyfinance.network.model;


import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.kasandco.familyfinance.app.item.ItemModel;

public class ItemApiModel {
    private String name;
    private boolean status;
    @SerializedName("is_delete")
    private boolean isDelete;
    @SerializedName("server_list")
    private long serverListId;
    @SerializedName("local_id")
    private long localId;
    private long id;
    @SerializedName("date_mod")
    private String dateMod;
    @SerializedName("image_name")
    private String serverImageName;
    @SerializedName("has_image")
    private boolean hasImage;

    public ItemApiModel(){}

    public ItemApiModel(@NonNull ItemModel item){
        localId = item.getId();
        id = item.getServerId()>0?item.getServerId():0;
        name = item.getName();
        status = item.getStatus() == 1;
        serverListId = item.getServerListId();
        dateMod = item.getDateMod();
        isDelete = item.getIsDelete() == 1;
        serverImageName = item.getServerImageName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public long getServerListId() {
        return serverListId;
    }

    public void setServerListId(long serverListId) {
        this.serverListId = serverListId;
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
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

    public String getServerImageName() {
        return serverImageName;
    }

    public void setServerImageName(String serverImageName) {
        this.serverImageName = serverImageName;
    }

    public boolean getHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }
}
