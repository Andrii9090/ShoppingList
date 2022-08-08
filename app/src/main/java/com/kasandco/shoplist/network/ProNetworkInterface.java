package com.kasandco.shoplist.network;

import com.kasandco.shoplist.core.Constants;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ProNetworkInterface {

    @GET(Constants.REST_API_VERSION+"user/pro-verification/{token}/")
    Call<ResponseBody> proVerif (@Path("token") String token);
}



