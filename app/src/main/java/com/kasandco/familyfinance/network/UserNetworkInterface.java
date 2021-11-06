package com.kasandco.familyfinance.network;

import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.network.model.ChangePaswordApiModel;
import com.kasandco.familyfinance.network.model.UpdateEmailApiModel;
import com.kasandco.familyfinance.network.model.UserTokenApiModel;
import com.kasandco.familyfinance.network.model.UserRegisterApiModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserNetworkInterface {
    @POST("auth/users/")
    Call<UserRegisterApiModel> createUser(@Body UserRegisterApiModel user);

    @POST("auth/token/login/")
    Call<UserTokenApiModel> login(@Body UserRegisterApiModel user);

    @POST("auth/users/set_email/")
    Call<ResponseBody> updateEmail(@Body UpdateEmailApiModel model);

    @POST("auth/users/set_password/")
    Call<ResponseBody> changePassword(@Body ChangePaswordApiModel model);

    @GET(Constants.REST_API_VERSION+"clear-history/")
    Call<ResponseBody> clearAllHistory(@Header("device-id") String deviceId);
}
