package com.kasandco.familyfinance.network;

import com.kasandco.familyfinance.app.finance.models.FinanceHistoryModel;
import com.kasandco.familyfinance.app.finance.models.FinanceHistorySync;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.network.model.FinanceCategoryApiModel;
import com.kasandco.familyfinance.network.model.FinanceHistoryApiModel;
import com.kasandco.familyfinance.network.model.LastSyncApiDataModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FinanceNetworkInterface {
    @POST(Constants.REST_API_VERSION + "finance/category/create/")
    Call<FinanceCategoryApiModel> createCategory(@Body FinanceCategoryApiModel category);

    @POST(Constants.REST_API_VERSION + "finance/category/update/")
    Call<FinanceCategoryApiModel> updateCategory(@Body FinanceCategoryApiModel category);

    @GET(Constants.REST_API_VERSION + "finance/category/delete/{pk}/")
    Call<ResponseBody> removeCategory(@Path("pk") long serverId);

    @POST(Constants.REST_API_VERSION + "finance/category/sync/")
    Call<List<FinanceCategoryApiModel>> syncCategory(@Body List<LastSyncApiDataModel> lastSyncData, @Header("device-id") String deviceId);

    @POST(Constants.REST_API_VERSION + "finance/history/create/")
    Call<FinanceHistoryApiModel> createItemHistory(@Body FinanceHistoryApiModel category);

    @POST(Constants.REST_API_VERSION + "finance/history/sync/")
    Call<List<FinanceHistoryApiModel>> syncHistory(@Body List<FinanceHistorySync> syncData, @Header("device-id") String deviceId);

    @GET(Constants.REST_API_VERSION + "finance/history/delete/{pk}/")
    Call<ResponseBody> removeHistoryItem(@Path("pk") Long serverId);
}
