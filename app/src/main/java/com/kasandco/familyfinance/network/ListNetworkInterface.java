package com.kasandco.familyfinance.network;

import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.network.model.LastSyncApiDataModel;
import com.kasandco.familyfinance.network.model.ListDataApiModel;

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
    Call<ListDataApiModel> createNewList(@Body ListDataApiModel list);

    @POST(Constants.REST_API_VERSION+"lists/update-list/")
    Call<ListDataApiModel> updateList(@Body ListDataApiModel list);

    @GET(Constants.REST_API_VERSION+"lists/delete-list/{id}/")
    Call<ResponseBody> removeList(@Path("id") long serverId);

    @POST(Constants.REST_API_VERSION+"lists/sync-lists/")
    Call<List<ListDataApiModel>> syncData(@Body List<LastSyncApiDataModel> lastSyncItems, @Header("device-id") String deviceId);

    @GET(Constants.REST_API_VERSION+"lists/subscribe-list/{token}/")
    Call<ListDataApiModel> subscribeToList(@Path("token") String token);

    @GET(Constants.REST_API_VERSION+"lists/list-items-delete/{status}/{listId}")
    Call<ResponseBody> clearListItems(@Path("listId") long listId, @Path("status") int status);
}
