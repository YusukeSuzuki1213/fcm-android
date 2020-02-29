package com.yusuke.fcm_ci_cd

import android.content.Context
import android.content.pm.PackageManager

class Constants() {

    companion object {
        // MyFirebaseMessagingServiceにおいてFCMからの取得した情報をMainActivityに送るためのアクション
        const val ACTION_FILTER = "android.intent.action.FILTER"
        // HTTPクライアント
        // const val HTTP_CLIENT = "CLIENT_RETROFIT2" // 使用するHTTPクライアント
        const val CLIENT_RETROFIT2 = "CLIENT_RETROFIT2"
        const val CLIENT_FUEL = "CLIENT_FUEL"
        // 非同期処理の方法
        const val ASYNC_CALLBACK = "ASYNC_CALLBACK"
        const val ASYNC_COROUTINE = "ASYNC_COROUTINE"
    }
}
