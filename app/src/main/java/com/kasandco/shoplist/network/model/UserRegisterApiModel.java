package com.kasandco.shoplist.network.model;

public class UserRegisterApiModel {
    private String token;

    public UserRegisterApiModel(String _token){
        token = _token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
