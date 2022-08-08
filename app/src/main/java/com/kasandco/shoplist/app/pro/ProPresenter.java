package com.kasandco.shoplist.app.pro;

import androidx.annotation.NonNull;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;

import java.util.List;

public class ProPresenter implements OnListenerLoadBillingData, ProServerListener{
    private ProActivity activity;
    private List<ProductDetails> productDetailsList;
    private BillingClientWrapper billingClientWrapper;
    private ProRepository repository;

    public ProPresenter(BillingClientWrapper billingClientWrapper){
        this.billingClientWrapper = billingClientWrapper;
    }

    public void viewReady(ProActivity activity){
        this.activity = activity;
        billingClientWrapper.connect(this);
        billingClientWrapper.getSubs();
    }

    @Override
    public void loaded(List<ProductDetails> productDetailsList) {
        this.productDetailsList = productDetailsList;
        activity.showPrice(productDetailsList.get(0).getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice());
    }

    public void payedError() {
        activity.showErrorDialog();
    }

    public void payedSuccess(List<Purchase> list) {
        for (Purchase purchase : list) {
            if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
                repository.verificationSubs(purchase, this);
            }
        }
    }

    private void acknowledgePurchase(Purchase purchase) {
        billingClientWrapper.billingClient.acknowledgePurchase(AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build(), new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    acknowledgePurchase(purchase);
                }else {
                    activity.showSuccessDialog();

                }
            }
        });
    }

    public void byeStart() {
        billingClientWrapper.startBilling(activity);
    }

    @Override
    public void success(Purchase purchase) {
        acknowledgePurchase(purchase);
    }

    @Override
    public void error() {
        activity.showErrorDialog();
    }
}
