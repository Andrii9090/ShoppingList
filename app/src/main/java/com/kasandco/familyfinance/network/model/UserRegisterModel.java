package com.kasandco.familyfinance.network.model;

public class UserRegisterModel {
    private String email;
    private String password;

    public UserRegisterModel(String _email, String _password){
        email = _email;
        password = _password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
