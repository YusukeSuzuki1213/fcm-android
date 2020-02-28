package com.zozo_tech.fcm_example.com

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.*

class NotificationUtil {
    companion object {
        private val VIBRATE_TIME_MS: LongArray = longArrayOf(100, 500)
        private const val FCM_CHANNEL_ID = "fcm_notification"
        private const val FCM_PUSH_CHANNEL_NAME = R.string.channel_name_fcm

        fun createChannel(context: Context) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                return
            }
            val manager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(createFcmPushChannel(context))
        }

        fun fcmLocalPush(context: Context, map: Map<String, String>) {
            val builder = Notification.Builder(context)
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            map["uri"]?.also {
                // URLが設定されている場合 -> 暗黙的なIntentを投げる
                val uri: Uri = Uri.parse(it)
                val intent: Intent = Intent("android.intent.action.VIEW", uri)
                builder.setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT))
            } ?: run {
                // URLが設定されていない場合 -> 通常遷移 MainActivityへ
                val intent: Intent = Intent(context, MainActivity::class.java)
                builder.setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT))
            }

            builder.apply {
                setSmallIcon(R.drawable.fcm_notification_icon)
                setContentTitle(context.getString(R.string.app_name))
                // OS５以上でヘッドアップ表示をするため、バイブレーションとプライオリティ設定
                setVibrate(VIBRATE_TIME_MS)
                setPriority(Notification.PRIORITY_HIGH)
                map["title"]?.let {
                    setContentText(it)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setChannelId(FCM_CHANNEL_ID)
                }
            }

            manager.notify(createID(), builder.build())
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        fun createFcmPushChannel(context: Context): NotificationChannel {
            val channel: NotificationChannel = NotificationChannel(
                FCM_CHANNEL_ID,
                context.getString(FCM_PUSH_CHANNEL_NAME), // 通知チャンネル名（端末のアプリ通知設定画面に表示されるチャンネル名）
                NotificationManager.IMPORTANCE_HIGH
            )

            return channel.apply {
                enableLights(true)
                lightColor = Color.WHITE

                enableVibration(true)
                vibrationPattern = VIBRATE_TIME_MS
            }
        }

        private fun createID(): Int {
            val now = Date()
            return Integer.parseInt(SimpleDateFormat("mmssSSSS",  Locale.JAPAN).format(now))
        }
    }
}
