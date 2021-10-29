package com.kasandco.familyfinance.network;

import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.network.model.LastSyncDataModel;
import com.kasandco.familyfinance.network.model.NetworkListData;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ListNetworkInterface {

    @POST(Constants.REST_API_VERSION+"lists/create-list/")
    Call<NetworkListData> createNewList(@Body NetworkListData list);

    @POST(Constants.REST_API_VERSION+"lists/update-list/")
    Call<NetworkListData> updateList(@Body NetworkListData list);

    @GET(Constants.REST_API_VERSION+"lists/delete-list/{id}/")
    Call<ResponseBody> removeList(@Path("id") long serverId);

    @POST(Constants.REST_API_VERSION+"lists/sync-lists/")
    Call<List<NetworkListData>> syncData(@Body List<LastSyncDataModel> lastSyncItems, @Header("device-id") String deviceId);

    @GET(Constants.REST_API_VERSION+"lists/subscribe-list/{token}/")
    Call<NetworkListData> subscribeToList(@Path("token") String token);

    @GET(Constants.REST_API_VERSION+"lists/list-items-delete/{status}/{listId}")
    Call<ResponseBody> clearListItems(@Path("listId") long listId, @Path("status") int status);
}
