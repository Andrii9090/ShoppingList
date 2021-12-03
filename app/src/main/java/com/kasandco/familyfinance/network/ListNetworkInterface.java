package com.kasandco.familyfinance.network;

import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.network.model.LastSyncApiDataModel;
import com.kasandco.familyfinance.network.model.ListApiModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ListNetworkInterface extends BaseNetworkInterface {

    @POST(Constants.REST_API_VERSION+"lists/create-list/")
    Call<ListApiModel> createNewList(@Body ListApiModel list);

    @POST(Constants.REST_API_VERSION+"lists/update-list/")
    Call<ListApiModel> updateList(@Body ListApiModel list);

    @GET(Constants.REST_API_VERSION+"lists/delete-list/{id}/")
    Call<ResponseBody> removeList(@Path("id") long serverId);

    @POST(Constants.REST_API_VERSION+"lists/sync-lists/")
    Call<List<ListApiModel>> syncData(@Body List<LastSyncApiDataModel> lastSyncItems, @Header("device-id") String deviceId);

    @GET(Constants.REST_API_VERSION+"lists/subscribe-list/{token}/")
    Call<ListApiModel> subscribeToList(@Path("token") String token);

    @GET(Constants.REST_API_VERSION+"lists/list-items-delete/{status}/{listId}")
    Call<ResponseBody> clearListItems(@Path("listId") long listId, @Path("status") int status);
}
