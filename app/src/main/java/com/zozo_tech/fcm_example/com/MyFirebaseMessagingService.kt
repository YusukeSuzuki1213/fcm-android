package com.zozo_tech.fcm_example.com

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        const val TAG = "MyFirebaseMessagingServ"
    }
    // InstanceIDが作られたら呼ばれる
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        // instanceIDを利用しているサーバ等に新しいtokenを送信する処理等
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            // Handle message within 10 seconds
            handleMessage(remoteMessage)
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    fun handleMessage(remoteMessage: RemoteMessage) {
        
    }



}