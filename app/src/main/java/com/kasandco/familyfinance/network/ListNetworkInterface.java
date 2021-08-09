package com.kasandco.familyfinance.network;

import com.kasandco.familyfinance.app.list.ListModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ListNetworkInterface {

    @POST("/lists/list/create")
    Call<ListModel> createNewList(@Body ListModel list, @Header("Api-Key") String key);

}
