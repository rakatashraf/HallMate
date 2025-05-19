package com.example.hallmate.services;

import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle FCM messages here
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Message: " + remoteMessage.getData());

        // You can add your custom logic to show notifications, etc.
    }

    @Override
    public void onNewToken(String token) {
        // Handle token updates
        Log.d(TAG, "Refreshed token: " + token);

        // You can send the new token to your server here if necessary.
    }
}
