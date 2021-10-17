package com.kasandco.familyfinance.network.model;

import com.google.gson.annotations.SerializedName;

public class ModelUpdateEmail {
    @SerializedName("re_new_email")
    private String emailOld;

    @SerializedName("new_email")
    private String emailNew;

    @SerializedName("current_password")
    private String password;

    public ModelUpdateEmail(String _emailOld, String _emailNew, String _password){
        emailOld = _emailOld;
        emailNew = _emailNew;
        password = _password;
    }
}
