package com.yusuke.fcm_ci_cd

import android.app.Application

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationUtil.createChannel(this)
    }
}
