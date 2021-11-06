package com.kasandco.familyfinance.network.model;

import com.google.gson.annotations.SerializedName;

public class UserTokenApiModel {

    @SerializedName("auth_token")
    public String token;

    public UserTokenApiModel(String _token){
        token = _token;
    }

    public String getToken(){
        return token;
    }
}
