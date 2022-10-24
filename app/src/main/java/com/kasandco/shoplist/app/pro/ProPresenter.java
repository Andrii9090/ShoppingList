package com.kasandco.shoplist.app.pro;

import android.app.AlertDialog;

import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;

import java.util.List;

public class ProPresenter implements OnListenerLoadBillingData, ProServerListener {
    private ProActivity activity;
    private List<ProductDetails> productDetailsList;
    private BillingClientWrapper billingClientWrapper;
    private ProRepository repository;

    public ProPresenter(BillingClientWrapper billingClientWrapper, ProRepository repository) {
        this.billingClientWrapper = billingClientWrapper;
        this.repository = repository;
    }

    public void viewReady(ProActivity activity) {
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
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                repository.verificationSubs(purchase.getPurchaseToken(), this);
            }
        }
    }

    public void byeStart() {
        billingClientWrapper.startBilling(activity);
    }

    @Override
    public void success() {
        activity.reloadApp();
    }

    @Override
    public void error() {
        activity.showErrorDialog();
    }
}
