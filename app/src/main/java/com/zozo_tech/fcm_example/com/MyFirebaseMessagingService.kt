package com.zozo_tech.fcm_example.com

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

/**
 * InstanceIDが更新された際呼ばれる
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        const val TAG = "MyFirebaseMessagingServ"
    }
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        // instanceIDを利用しているサーバ等に新しいtokenを送信する処理等
    }


}