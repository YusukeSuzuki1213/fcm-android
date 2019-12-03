package com.zozo_tech.fcm_example.com

import android.content.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.zozo_tech.fcm_example.com.Constants.Companion.ACTION_FILTER
import com.zozo_tech.fcm_example.com.Constants.Companion.SLACK_WEBHOOK_URL

class MainActivity : AppCompatActivity() , View.OnClickListener {

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
                        Log.d(TAG, "intent extras key : ${ bundle.get(key) }")
                        text += "${key}: ${bundle.get(key)}, \n"
                    }
                    instanceInfoTextView.text =  text
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
                val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                // Get new Instance ID or token
                when(view?.id) {
                    R.id.getInstanceIdButton -> {
                        instanceInfoTextView.text = getString(R.string.msg_instance_id_fmt, task.result?.id)
                        Snackbar.make(view!!, getString(R.string.msg_copied_clipboard_fmt, "Instance ID"), Snackbar.LENGTH_LONG).show()
                        clipboard.primaryClip = ClipData.newPlainText("label", task.result?.id)
                    }
                    R.id.getInstanceTokenButton -> {
                        instanceInfoTextView.text = getString(R.string.msg_instance_token_fmt, task.result?.token)
                        Snackbar.make(view!!, getString(R.string.msg_copied_clipboard_fmt, "Token ID"), Snackbar.LENGTH_LONG).show()
                        clipboard.primaryClip = ClipData.newPlainText("label", task.result?.token)
                    }
                    R.id.getInstanceIdAndTokenButton -> {instanceInfoTextView.text = getString(R.string.msg_instance_id_and_token_fmt, task.result?.id, task.result?.token)
                        //Snackbar.make(view!!, getString(R.string.msg_copied_clipboard_fmt, "Token ID"), Snackbar.LENGTH_LONG).show()

                        var messageMap = mutableMapOf<String, String?>()
                        getDeviceInfo(messageMap)// 参照渡し
                        messageMap["Token"] = task.result?.token
                        messageMap["InstanceID"] = task.result?.id

                        runCatching {
                            sendMessage(messageMap)
                        }.onSuccess {
                            Snackbar.make(view!!, getString(R.string.slack_http_request_success), Snackbar.LENGTH_LONG).show()
                        }.onFailure {
                            Snackbar.make(view!!, getString(R.string.slack_http_request_failure), Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            })
    }

    private fun getDeviceInfo(map: MutableMap<String, String?>) {
        map["端末名"] = Build.MODEL
    }

    private fun sendMessage(map: MutableMap<String, String?>) {
        var content: String = ""
        map.forEach{
            content += "${it.key}: ${it.value}\n"
        }

        val body: String = getString(R.string.slack_payload, content)

        // HTTPリクエスト
        Fuel.post(SLACK_WEBHOOK_URL).
            jsonBody(body).
            responseString { _, _, result ->
                result.fold({ _ ->

                }, { err ->
                    Log.d("http", err.message)
                    throw Exception("sss")//TODO: 呼び出し側にスローされない
                })
            }
    }
}
