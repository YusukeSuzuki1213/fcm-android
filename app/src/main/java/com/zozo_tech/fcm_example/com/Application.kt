package com.zozo_tech.fcm_example.com

import android.app.Application

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationUtil.createChannel(this)
    }
}
