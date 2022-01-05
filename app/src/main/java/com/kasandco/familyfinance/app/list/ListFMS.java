package com.kasandco.familyfinance.app.list;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class ListFMS extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("TAG", "From: " + remoteMessage.getData().toString());
        Log.e("TAG", "From: " + remoteMessage.getFrom());
    }
}
