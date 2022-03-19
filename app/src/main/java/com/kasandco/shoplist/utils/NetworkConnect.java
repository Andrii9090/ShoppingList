package com.kasandco.shoplist.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.kasandco.shoplist.R;


public class NetworkConnect {
    private Context context;
    private ConnectivityManager cm;
    private NetworkInfo netInfo;

    public NetworkConnect(Context context) {
        this.context = context;
    }


    public boolean isInternetAvailable() {
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (isNetworkConnected()){
            return true;
        }

        netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (isNetworkConnected()){
            return true;
        }

        netInfo = cm.getActiveNetworkInfo();

        return isNetworkConnected();

    }

    private boolean isNetworkConnected (){
        return netInfo!=null && netInfo.isConnected();
    }

}
