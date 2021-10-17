package com.kasandco.familyfinance.network.model;

import com.google.gson.annotations.SerializedName;

public class ModelChangePassword {


        @SerializedName("re_new_password")
        private String newPassword2;

        @SerializedName("new_password")
        private String newPassword;

        @SerializedName("current_password")
        private String password;

        public ModelChangePassword(String _newPassword, String _newPassword2, String _password){
            newPassword = _newPassword;
            newPassword2 = _newPassword2;
            password = _password;
        }

}
