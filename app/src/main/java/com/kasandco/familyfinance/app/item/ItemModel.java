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
    private String name;
    @ColumnInfo(name = "date_mod")
    private String dateMod;
    private String quantity;
    @ColumnInfo(name = "image_path")
    private String imagePath;
    private int status;

    public ItemModel(){}

    @Ignore
    public ItemModel(String name, String quantity){
        this.name = name;
        this.quantity = quantity;
        this.status=1;
        this.dateMod = String.valueOf(System.currentTimeMillis());
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    @ColumnInfo(name = "is_delete")
    private int isDelete;

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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
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
        newModel.setQuantity(quantity);
        newModel.setImagePath(imagePath);
        newModel.setStatus(status);
        newModel.setIsDelete(isDelete);
        newModel.setId(id);
        //TODO Сделать серверный айди
        return newModel;
    }
}
