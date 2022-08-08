package com.kasandco.shoplist.app.pro;

import com.android.billingclient.api.ProductDetails;

import java.util.List;

public interface OnListenerLoadBillingData {
    void loaded(List<ProductDetails> productDetailsList);

}
