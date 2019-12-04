package com.zozo_tech.fcm_example.com

class Constants {
    companion object {
        // MyFirebaseMessagingServiceにおいてFCMからの取得した情報をMainActivityに送るためのアクション
        const val ACTION_FILTER = "android.intent.action.FILTER"
        // SlackにWebhookeIDとかを送る
        // 開発用
        const val SLACK_WEBHOOK_URL = ""
        // 本番用
        //const val SLACK_WEBHOOK_URL = ""
    }
}