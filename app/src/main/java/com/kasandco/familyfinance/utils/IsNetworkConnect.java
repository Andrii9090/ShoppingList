package com.kasandco.familyfinance.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.kasandco.familyfinance.R;

public class IsNetworkConnect {
    Context context;

    public IsNetworkConnect(Context context) {
        this.context = context;
    }


    public boolean isInternetAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean isConnect = netInfo != null && netInfo.isConnectedOrConnecting();
        if (isConnect) {
            return true;
        } else {
            ToastUtils.showToast(context.getString(R.string.text_error_connect_to_internet), context);
            return false;
        }
    }
}
