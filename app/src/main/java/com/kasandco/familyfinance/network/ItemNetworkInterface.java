package com.kasandco.familyfinance.network;

import com.kasandco.familyfinance.app.item.ItemSyncHistoryModel;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.network.model.ImageItemModelResponse;
import com.kasandco.familyfinance.network.model.ItemModelResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ItemNetworkInterface {
    @POST(Constants.REST_API_VERSION+"lists/items/create/")
    Call<ItemModelResponse> create(@Body ItemModelResponse item);

    @POST(Constants.REST_API_VERSION+"lists/items/update/")
    Call<List<ItemModelResponse>> update(@Body List<ItemModelResponse> items);

    @POST(Constants.REST_API_VERSION+"lists/items/image/upload/")
    Call<ImageItemModelResponse> sendImage(@Body ImageItemModelResponse item);

    @POST(Constants.REST_API_VERSION+"lists/items/sync/")
    Call<List<ItemModelResponse>> sync(@Body List<ItemSyncHistoryModel> itemsSync, @Header("device-id") String deviceId, @Header("list-id") long serverListId);

    @GET(Constants.REST_API_VERSION+"lists/items/delete/{id}/")
    Call<ResponseBody> remove(@Path("id") long serverId);

    @GET(Constants.REST_API_VERSION+"lists/items/image/delete/{id}/")
    Call<ResponseBody> removeImage(@Path("id") long serverId);

    @GET(Constants.REST_API_VERSION+"lists/items/image/download/{id}/")
    Call<ImageItemModelResponse> downloadImage(@Path("id") long serverId);
}
