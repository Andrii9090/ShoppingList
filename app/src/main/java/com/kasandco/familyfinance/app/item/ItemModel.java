package com.kasandco.familyfinance.app.item;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "list_item")
public class ItemModel implements Cloneable{
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "server_id", defaultValue = "0")
    private long serverId;
    private String name;
    @ColumnInfo(name = "date_mod")
    private String dateMod;
    @ColumnInfo(name = "image_path")
    private String imagePath;
    private int status;
    @ColumnInfo(name = "is_delete", defaultValue = "0")
    private byte isDelete;
    @ColumnInfo(name = "server_list_id", defaultValue = "0")
    private long serverListId;
    @ColumnInfo(name="local_list_id")
    private long localListId;

    public ItemModel(){}

    @Ignore
    public ItemModel(String name, long listId){
        this.name = name;
        this.status=1;
        this.localListId=listId;
        this.dateMod = String.valueOf(System.currentTimeMillis());
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public byte getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(byte isDelete) {
        this.isDelete = isDelete;
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

    public String getDateMod() {
        return dateMod;
    }

    public void setDateMod(String time) {
        this.dateMod = time;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    public ItemModel clone() {
        ItemModel newModel = new ItemModel();
        newModel.setDateMod(dateMod);
        newModel.setName(name);
        newModel.setImagePath(imagePath);
        newModel.setStatus(status);
        newModel.setIsDelete(isDelete);
        newModel.setId(id);
        newModel.setServerId(serverId);
        newModel.setLocalListId(localListId);
        newModel.setServerListId(serverListId);
        return newModel;
    }

    public long getServerListId() {
        return serverListId;
    }

    public void setServerListId(long serverListId) {
        this.serverListId = serverListId;
    }

    public long getLocalListId() {
        return localListId;
    }

    public void setLocalListId(long localListId) {
        this.localListId = localListId;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }
}
