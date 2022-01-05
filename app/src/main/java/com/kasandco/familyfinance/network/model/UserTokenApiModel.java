package com.kasandco.familyfinance.network.model;

import com.google.gson.annotations.SerializedName;

public class UserTokenApiModel {

    @SerializedName("auth_token")
    private String token;

    public UserTokenApiModel(String _token){
        token = _token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken(){
        return token;
    }
}
