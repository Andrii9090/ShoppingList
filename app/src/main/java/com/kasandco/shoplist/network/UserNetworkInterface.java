package com.kasandco.shoplist.network;

import com.kasandco.shoplist.app.user.group.ModelGroup;
import com.kasandco.shoplist.core.Constants;
import com.kasandco.shoplist.network.model.UIdModel;
import com.kasandco.shoplist.network.model.UserApiModel;
import com.kasandco.shoplist.network.model.UserSettingsApiModel;
import com.kasandco.shoplist.network.model.UserTokenApiModel;
import com.kasandco.shoplist.network.model.UserRegisterApiModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserNetworkInterface {
    @POST("api/v1/token/")
    Call<UserTokenApiModel> login(@Body UserRegisterApiModel user);

    @GET(Constants.REST_API_VERSION+"clear-history/")
    Call<ResponseBody> clearAllHistory(@Header("device-id") String deviceId);

    @GET(Constants.REST_API_VERSION+"logout-all/")
    Call<ResponseBody> logoutAll();

    @POST(Constants.REST_API_VERSION+"user/save-settings/")
    Call<ResponseBody> saveSettings(@Body UserSettingsApiModel settings, @Header("device-id") String deviceId);

    @GET(Constants.REST_API_VERSION+"user/get-settings/")
    Call<UserSettingsApiModel> getSettings(@Header("Authorization") String token, @Header("device-id") String deviceId);


    @GET(Constants.REST_API_VERSION+"user/get-group/")
    Call<ModelGroup> getGroup();


    @POST(Constants.REST_API_VERSION+"user/remove-from-group/")
    Call<ResponseBody> removeFromGroup(@Body UserApiModel user);

    @GET(Constants.REST_API_VERSION+"user/get-uid/")
    Call<UIdModel> getUid();

    @GET(Constants.REST_API_VERSION+"user/add-to-group/{uid}/")
    Call<ResponseBody> addToGroup(@Path("uid") String uid);
}
