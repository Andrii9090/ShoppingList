package com.kasandco.shoplist.network;

import com.kasandco.shoplist.app.item.ItemSyncHistoryModel;
import com.kasandco.shoplist.core.Constants;
import com.kasandco.shoplist.network.model.ImageItemApiModel;
import com.kasandco.shoplist.network.model.ItemApiModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ItemNetworkInterface {
    @POST(Constants.REST_API_VERSION+"lists/items/create/")
    Call<ItemApiModel> create(@Body ItemApiModel item);

    @POST(Constants.REST_API_VERSION+"lists/items/update/")
    Call<ItemApiModel> update(@Body ItemApiModel item);

    @POST(Constants.REST_API_VERSION+"lists/items/image/upload/")
    Call<ImageItemApiModel> sendImage(@Body ImageItemApiModel item);

    @POST(Constants.REST_API_VERSION+"lists/items/sync/")
    Call<List<ItemApiModel>> sync(@Body List<ItemSyncHistoryModel> itemsSync, @Header("device-id") String deviceId, @Header("list-id") long serverListId);

    @GET(Constants.REST_API_VERSION+"lists/items/delete/{id}/")
    Call<ResponseBody> remove(@Path("id") long serverId);

    @GET(Constants.REST_API_VERSION+"lists/items/image/delete/{id}/")
    Call<ResponseBody> removeImage(@Path("id") long serverId);

    @GET(Constants.REST_API_VERSION+"lists/items/image/download/{id}/")
    Call<ImageItemApiModel> downloadImage(@Path("id") long serverId);
}
