package com.kasandco.familyfinance.network;

import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.network.model.ModelChangePassword;
import com.kasandco.familyfinance.network.model.ModelUpdateEmail;
import com.kasandco.familyfinance.network.model.NetworkListData;
import com.kasandco.familyfinance.network.model.ResponseUserTokenModel;
import com.kasandco.familyfinance.network.model.UserRegisterModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserNetworkInterface {
    @POST("auth/users/")
    Call<UserRegisterModel> createUser(@Body UserRegisterModel user);

    @POST("auth/token/login/")
    Call<ResponseUserTokenModel> login(@Body UserRegisterModel user);

    @POST("auth/users/set_email/")
    Call<ResponseBody> updateEmail(@Body ModelUpdateEmail model);

    @POST("auth/users/set_password/")
    Call<ResponseBody> changePassword(@Body ModelChangePassword model);

    @GET(Constants.REST_API_VERSION+"clear-history/")
    Call<ResponseBody> clearAllHistory(@Header("device-id") String deviceId);
}
