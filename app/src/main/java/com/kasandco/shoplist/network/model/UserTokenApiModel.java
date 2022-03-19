package com.kasandco.shoplist.network.model;

import com.google.gson.annotations.SerializedName;

public class UserTokenApiModel {

    @SerializedName("auth_token")
    private String token;
    private String email;

    public UserTokenApiModel(String _token){
        token = _token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken(){
        return token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
