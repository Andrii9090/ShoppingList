package com.kasandco.shoplist.app.pro;

import com.android.billingclient.api.Purchase;

public interface ProServerListener {
    void success(Purchase purchase);
    void error();
}
