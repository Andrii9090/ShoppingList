package com.kasandco.shoplist.app.pro;

import com.android.billingclient.api.Purchase;
import com.kasandco.shoplist.core.Constants;
import com.kasandco.shoplist.network.ProNetworkInterface;
import com.kasandco.shoplist.network.Requests;
import com.kasandco.shoplist.network.model.ListApiModel;
import com.kasandco.shoplist.utils.SharedPreferenceUtil;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

@ProScope
public class ProRepository {

    private SharedPreferenceUtil sharedPreferenceUtil;
    private ProNetworkInterface network;

    @Inject
    public ProRepository(Retrofit retrofit, SharedPreferenceUtil _sharedPrefUtils){
        network = retrofit.create(ProNetworkInterface.class);
        sharedPreferenceUtil = _sharedPrefUtils;
    }

    public void verificationSubs(Purchase purchase, ProServerListener callback){
        new Thread(() -> {
            Call<ResponseBody> call = network.proVerif(purchase.getPurchaseToken());
            Requests.RequestsInterface<ResponseBody> callbackResponse = new Requests.RequestsInterface<ResponseBody>() {
                @Override
                public void success(ResponseBody obj) {
                    callback.success(purchase);
                    sharedPreferenceUtil.getEditor().putBoolean(Constants.IS_PRO, true);
                }

                @Override
                public void error() {
                    callback.error();
                }

                @Override
                public void noPermit() {
                    callback.error();
                }

            };
            Requests.request(call, callbackResponse);
        }).start();
    }

}
