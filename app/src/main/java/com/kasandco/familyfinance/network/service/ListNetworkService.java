package com.kasandco.familyfinance.network.service;

import android.content.Context;

import com.kasandco.familyfinance.app.list.ListModel;
import com.kasandco.familyfinance.app.list.ListDao;
import com.kasandco.familyfinance.network.ListNetworkInterface;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListNetworkService extends BaseNetworkService{

    @Inject
    public ListNetworkService(){

    }
    @Inject
    ListNetworkInterface network;

    @Inject
    @Named("context")
    Context context;

    @Inject
    ListDao listDao;

    public void createList(ListModel list){
        Call<ListModel> call = network.createNewList(list, getApiKey());
        call.enqueue(new Callback<ListModel>() {
            @Override
            public void onResponse(Call<ListModel> call, Response<ListModel> response) {
                if(response.isSuccessful()){

                }
            }

            @Override
            public void onFailure(Call<ListModel> call, Throwable t) {

            }
        });
    }
}
