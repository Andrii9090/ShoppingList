package com.kasandco.shoplist.app.pro;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.firebase.crashlytics.internal.model.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class BillingClientWrapper implements PurchasesResponseListener {

    public BillingClient billingClient;
    private List<ProductDetails> productDetailsList;
    private OnListenerLoadBillingData listenerLoadBillingData;

    public BillingClientWrapper(BillingClient billingClient){
        this.billingClient = billingClient;
    }

    public void connect(OnListenerLoadBillingData listener){
        listenerLoadBillingData = listener;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    ArrayList<QueryProductDetailsParams.Product> list = new ArrayList<>();
                    list.add(QueryProductDetailsParams.Product.newBuilder()
                            .setProductId("test_1_month")
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build());

                    QueryProductDetailsParams queryProductDetailsParams =
                            QueryProductDetailsParams.newBuilder()
                                    .setProductList(list)
                                    .build();

                    billingClient.queryProductDetailsAsync(
                            queryProductDetailsParams,
                            (billingResult1, productDetailsList) -> {
                                BillingClientWrapper.this.productDetailsList = productDetailsList;
                                listener.loaded(productDetailsList);
                            }
                    );
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                connect(listener);
            }
        });
    }

    public void getSubs() {
        billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(),this);
    }

    @Override
    public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
        Log.e("bil", billingResult.getDebugMessage());

    }

    public void startBilling(Activity activity) {
        String offerToken = productDetailsList.get(0)
                .getSubscriptionOfferDetails()
                .get(0)
                .getOfferToken();

        List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.from(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetailsList.get(0))
                                .setOfferToken(offerToken)
                                .build()
                );
        BillingResult billingResult = billingClient.launchBillingFlow(activity, BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList).build());
    }

}
