package com.kasandco.shoplist.app.pro;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.kasandco.shoplist.App;
import com.kasandco.shoplist.R;
import com.kasandco.shoplist.app.BaseActivity;
import com.kasandco.shoplist.utils.SharedPreferenceUtil;

import java.util.List;

import javax.inject.Inject;

public class ProActivity extends BaseActivity implements PurchasesUpdatedListener {

    public Button btnByePro;
    @Inject
    public ProPresenter presenter;

    @Inject
    SharedPreferenceUtil sharedPreferenceUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getProComponent(this).inject(this);
        setContentView(R.layout.activity_pro);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        presenter.viewReady(this);
        btnByePro = findViewById(R.id.btn_bye_pro);
        Toolbar toolbar = findViewById(R.id.item_activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.findViewById(R.id.toolbar_menu).setOnClickListener(view -> drawerLayout.openDrawer(Gravity.LEFT));
    }

    @Override
    protected void startNewActivity(Class<?> activityClass) {
        if (getClass() != activityClass) {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
        } else {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    @SuppressLint("SetTextI18n")
    public void showPrice(String price) {
        btnByePro.setText(price + " / " + getString(R.string.btn_pro_period));
        btnByePro.setOnClickListener(v -> presenter.byeStart());
    }

    public void showErrorDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).setMessage(R.string.error_for_pay_subscr)
                .setPositiveButton("OK", (dialog1, which) -> {
                    dialog1.cancel();
                }).create();
        dialog.show();
    }

    public void showSuccessDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).setMessage(R.string.payed_success)
                .setPositiveButton("OK", (dialog1, which) -> {
                    dialog1.cancel();
                }).create();
        dialog.show();
    }
    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (BillingClient.BillingResponseCode.OK == billingResult.getResponseCode()) {
            presenter.payedSuccess(list);
        } else {
            presenter.payedError();
        }
    }

}
