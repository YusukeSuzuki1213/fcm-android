package com.yusuke.fcm_ci_cd

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.yusuke.fcm_ci_cd.Constants.Companion.ACTION_FILTER

class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        const val TAG = "MyFirebaseMessagingServ"
    }
    // InstanceIDが作られたら呼ばれる
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            NotificationUtil.fcmLocalPush(application, remoteMessage.data)
            handleMessage(remoteMessage.data)
        }
    }

    private fun handleMessage(data: Map<String, String>) {
        val intent: Intent = Intent(ACTION_FILTER)
        for ((key, value) in data) {
            intent.putExtra(key, value)
        }
        // TODO: deprecatedなのでLiveDataで実装
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}
