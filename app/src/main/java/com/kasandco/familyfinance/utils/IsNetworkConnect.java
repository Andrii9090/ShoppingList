package com.kasandco.familyfinance.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import com.kasandco.familyfinance.R;


public class IsNetworkConnect {
    private Context context;
    private ConnectivityManager cm;
    private NetworkInfo netInfo;

    public IsNetworkConnect(Context context) {
        this.context = context;
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
    }


    public boolean isInternetAvailable() {
        boolean isConnect = netInfo != null && netInfo.isConnectedOrConnecting();
        if (isConnect) {
            return true;
        } else {
            Handler handler = new android.os.Handler(Looper.getMainLooper());
            handler.post(() -> ToastUtils.showToast(context.getString(R.string.text_error_connect_to_internet), context));
            return false;
        }
    }
}
