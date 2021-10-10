package com.kasandco.familyfinance.network;

import com.kasandco.familyfinance.network.model.ResponseUserTokenModel;
import com.kasandco.familyfinance.network.model.UserRegisterModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserNetworkInterface {
    @POST("auth/users/")
    Call<UserRegisterModel> createUser(@Body UserRegisterModel user);

    @POST("auth/token/login/")
    Call<ResponseUserTokenModel> login(@Body UserRegisterModel user);
}
