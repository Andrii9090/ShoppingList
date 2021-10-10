package com.kasandco.familyfinance.network;

import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.network.model.NetworkListData;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ListNetworkInterface {

    @POST(Constants.REST_API_VERSION+"lists/create-list/")
    Call<NetworkListData> createNewList(@Body NetworkListData list);

    @GET(Constants.REST_API_VERSION+"lists/delete-list/{id}/")
    Call<ResponseBody> removeList(@Path("id") long serverId);

    @POST(Constants.REST_API_VERSION+"lists/sync-lists/")
    Call<List<NetworkListData>> syncData(@Body List<NetworkListData> lists);

    @GET(Constants.REST_API_VERSION+"lists/subscribe-list/{token}/")
    Call<NetworkListData> subscribeToList(@Path("token") String token);
}
