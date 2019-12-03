package com.zozo_tech.fcm_example.com

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.zozo_tech.fcm_example.com.Constants.Companion.ACTION_FILTER


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
        Log.d(TAG, "From: ${remoteMessage.from}")
        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            // Handle message within 10 seconds
            // textViewにdata payloadを表示
            // handleMessage(remoteMessage.data)
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }

    private fun handleMessage(data: Map<String, String>) {
        val intent: Intent = Intent(ACTION_FILTER)
        for((key, value) in data) {
            intent.putExtra(key, value)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}