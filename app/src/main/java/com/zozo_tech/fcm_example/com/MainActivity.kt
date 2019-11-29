package com.zozo_tech.fcm_example.com

import android.content.*
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

class MainActivity : AppCompatActivity() , View.OnClickListener {

    companion object {
        private const val TAG = "MainActivity"
        const val FILTER= "android.intent.action.FILTER"
    }

    private lateinit var receiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getInstanceIdButton.setOnClickListener(this)
        getInstanceTokenButton.setOnClickListener(this)

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
                        instanceIdTextView.text = getString(R.string.msg_instance_id_fmt, task.result?.id)
                        Snackbar.make(view!!, getString(R.string.msg_copied_clipboard_fmt, "Instance ID"), Snackbar.LENGTH_LONG).show()
                        clipboard.primaryClip = ClipData.newPlainText("label", task.result?.id)
                    }
                    R.id.getInstanceTokenButton -> {
                        instanceIdTextView.text = getString(R.string.msg_instance_token_fmt, task.result?.token)
                        Snackbar.make(view!!, getString(R.string.msg_copied_clipboard_fmt, "Token ID"), Snackbar.LENGTH_LONG).show()
                        clipboard.primaryClip = ClipData.newPlainText("label", task.result?.token)
                    }
                }



                //clipboard.primaryClip = ClipData.newPlainText("label", etEditText01.text)
            })

    }
}
