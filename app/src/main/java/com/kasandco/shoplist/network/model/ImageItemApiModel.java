package com.kasandco.shoplist.network.model;

import com.google.gson.annotations.SerializedName;

public class ImageItemApiModel {
    private long id;
    @SerializedName("image_path")
    private String image;
    @SerializedName("image_name")
    private String imageName;

    public ImageItemApiModel(long _serverId, String _image){
        id = _serverId;
        image = _image;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
