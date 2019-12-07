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
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.zozo_tech.fcm_example.com.ConstansSecret.Companion.SLACK_COMMON_WEBHOOK_URI
import com.zozo_tech.fcm_example.com.ConstansSecret.Companion.SLSCK_WEBHOOK_DEV_CHANNEL
import kotlinx.android.synthetic.main.activity_main.*
import com.zozo_tech.fcm_example.com.Constants.Companion.ACTION_FILTER
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.POST
import java.lang.Exception

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
                        text += "${key}: ${bundle.get(key)}, \n"
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
                        getDeviceInfo(messageMap)// 参照渡し
                        messageMap["InstanceID"] = task.result?.id
                        messageMap["Token"] = task.result?.token

                        var content: String = ""
                        messageMap.forEach {
                            content += "${it.key}: ${it.value}\n"
                        }

                        val body: String = getString(R.string.slack_payload, content)

                        sendMessage(body,
                            onSuccess = {
                                Snackbar.make(view, getString(R.string.slack_http_request_success), Snackbar.LENGTH_LONG).show()
                            },
                            onError = {
                                Snackbar.make(view, getString(R.string.slack_http_request_failure), Snackbar.LENGTH_LONG).show()
                            })
                    }
                }
            })
    }

    private fun getDeviceInfo(map: MutableMap<String, String?>) {
        map["端末名"]  = Build.MODEL
        map["製造者名"] = Build.MANUFACTURER
    }

    private fun sendMessage(body: String, onSuccess: () -> Unit, onError: () -> Unit) {

        // HTTPリクエスト
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(SLACK_COMMON_WEBHOOK_URI)
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .build()

        val service: SlackService = retrofit.create(SlackService::class.java)

        service.sendSlackWebHook(SlackWebHook(body)).enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    when(response.isSuccessful){
                        true  -> onSuccess.invoke() // HTTPリクエストのレスポンスのステータスコードが200番台
                        false -> onError.invoke() // ステータスコードがそれ以外
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    onError.invoke()
                }
            }
        )
    }
}

data class SlackWebHook(
     val text: String
)

interface SlackService {
    @POST(SLSCK_WEBHOOK_DEV_CHANNEL)
    fun sendSlackWebHook(@Body slackWebHook: SlackWebHook): Call<String>
}


