package com.zozo_tech.fcm_example.com

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        const val FILTER= "android.intent.action.FILTER"
    }

    private lateinit var receiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // InstanceIDを取得
        getInstanceButton.setOnClickListener {
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token

                    val msg = getString(R.string.msg_token_fmt, token)
                    Log.d(TAG, msg)

                    instanceIdTextView.text = msg
                })
        }

        // recieverの設定
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.extras?.let { bundle ->
                    var text: String = ""
                    for (key in bundle.keySet()) {
                        // UIの更新
                        Log.d(TAG, "intent extras key : ${ bundle.get(key) }")
                        text += "${key}: ${bundle.get(key)}, \n"
                    }
                    instanceIdTextView.text =  text
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(FILTER)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }
}
