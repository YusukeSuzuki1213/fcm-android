package com.yusuke.fcm_ci_cd

import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.yusuke.fcm_ci_cd.Constants.Companion.ACTION_FILTER
import com.yusuke.fcm_ci_cd.Constants.Companion.ASYNC_CALLBACK
import com.yusuke.fcm_ci_cd.Constants.Companion.ASYNC_COROUTINE
import com.yusuke.fcm_ci_cd.Constants.Companion.CLIENT_FUEL
import com.yusuke.fcm_ci_cd.Constants.Companion.CLIENT_RETROFIT2
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var receiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getInstanceIdButton.setOnClickListener(this)
        getInstanceTokenButton.setOnClickListener(this)
        getInstanceIdAndTokenButton.setOnClickListener(this)

        // recieverの設定
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.extras?.let { bundle ->
                    var text: String = ""
                    for (key in bundle.keySet()) {
                        // UIの更新
                        Log.d(TAG, "intent extras key : ${bundle.get(key)}")
                        text += "$key: ${bundle.get(key)}, \n"
                    }
                    instanceInfoTextView.text = text
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_FILTER)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    override fun onClick(view: View?) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                val clipboard: ClipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                when (view?.id) {
                    R.id.getInstanceIdButton -> {
                        instanceInfoTextView.text =
                            getString(R.string.msg_instance_id_fmt, task.result?.id)
                        Snackbar.make(
                            view!!,
                            getString(R.string.msg_copied_clipboard_fmt, "Instance ID"),
                            Snackbar.LENGTH_LONG
                        ).show()
                        clipboard.primaryClip = ClipData.newPlainText("label", task.result?.id)
                    }
                    R.id.getInstanceTokenButton -> {
                        instanceInfoTextView.text =
                            getString(R.string.msg_instance_token_fmt, task.result?.token)
                        Snackbar.make(
                            view!!,
                            getString(R.string.msg_copied_clipboard_fmt, "Token ID"),
                            Snackbar.LENGTH_LONG
                        ).show()
                        clipboard.primaryClip = ClipData.newPlainText("label", task.result?.token)
                    }
                    R.id.getInstanceIdAndTokenButton -> {
                        instanceInfoTextView.text = getString(
                            R.string.msg_instance_id_and_token_fmt,
                            task.result?.id,
                            task.result?.token
                        )

                        var messageMap = mutableMapOf<String, String?>()
                        getDeviceInfo(messageMap) // 参照渡し
                        messageMap["InstanceID"] = task.result?.id
                        messageMap["Token"] = task.result?.token

                        var content: String = ""
                        messageMap.forEach {
                            content += "${it.key}: ${it.value}\n"
                        }

                        val body: String = getString(R.string.slack_payload, content)

                        sendMessage(body,
                            onSuccess = {
                                Snackbar.make(
                                    view,
                                    getString(R.string.slack_http_request_success),
                                    Snackbar.LENGTH_LONG
                                ).show()
                            },
                            onError = {
                                Snackbar.make(
                                    view,
                                    getString(R.string.slack_http_request_failure),
                                    Snackbar.LENGTH_LONG
                                ).show()
                            })
                    }
                }
            })
    }

    private fun getDeviceInfo(map: MutableMap<String, String?>) {
        map["端末名"] = Build.MODEL
        map["製造者名"] = Build.MANUFACTURER
    }

    private fun sendMessage(body: String, onSuccess: () -> Unit, onError: () -> Unit) {
        val requestClient: RequestClient = RequestClient(onSuccess, onError)
        // Retrofit &　コールバック & 開発用WEBHOOK URL
        requestClient.post(
            client = CLIENT_RETROFIT2,
            asyncMethod = ASYNC_CALLBACK,
            baseUrl = BuildConfig.SLACK_COMMON_WEBHOOK_URI,
            resourceUrl = BuildConfig.SLACK_WEBHOOK_DEV_CHANNEL,
            body = body
        )
        // Retrofit2 &　Coroutines & 開発用WEBHOOK URL
        requestClient.post(
            client = CLIENT_RETROFIT2,
            asyncMethod = ASYNC_COROUTINE,
            baseUrl = BuildConfig.SLACK_COMMON_WEBHOOK_URI,
            resourceUrl = BuildConfig.SLACK_WEBHOOK_DEV_CHANNEL,
            body = body
        )
        // Fuel & コールバック & 開発用WEBHOOK URL
        requestClient.post(
            client = CLIENT_FUEL,
            asyncMethod = ASYNC_CALLBACK,
            baseUrl = BuildConfig.SLACK_COMMON_WEBHOOK_URI,
            resourceUrl = BuildConfig.SLACK_WEBHOOK_DEV_CHANNEL,
            body = body
        )
        // Fuel & Coroutines & 開発用WEBHOOK URL
        requestClient.post(
            client = CLIENT_FUEL,
            asyncMethod = ASYNC_COROUTINE,
            baseUrl = BuildConfig.SLACK_COMMON_WEBHOOK_URI,
            resourceUrl = BuildConfig.SLACK_WEBHOOK_DEV_CHANNEL,
            body = body
        )
    }
}
