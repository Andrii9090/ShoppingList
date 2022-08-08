package com.kasandco.shoplist.app.pro;

import android.content.Context;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.PurchasesUpdatedListener;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ProModule {

    private final Context activityContext;

    public ProModule(@Named("activity_context") Context context){
        this.activityContext = context;
    }

    @Provides
    @ProScope
    public BillingClient providesBillingClient(){
        return BillingClient
                .newBuilder(activityContext.getApplicationContext())
                .enablePendingPurchases()
                .setListener((PurchasesUpdatedListener) activityContext)
                .build();
    }

    @Provides
    @ProScope
    public ProPresenter providesProPresenter(BillingClientWrapper billingClientWrapper){
        return new ProPresenter(billingClientWrapper);
    }

    @Provides
    @ProScope
    public BillingClientWrapper providesBillingClientWrapper(BillingClient billingClient){
        return new BillingClientWrapper(billingClient);
    }
}
