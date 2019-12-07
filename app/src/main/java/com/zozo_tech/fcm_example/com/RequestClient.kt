package com.zozo_tech.fcm_example.com

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.squareup.moshi.Moshi
import com.zozo_tech.fcm_example.com.ConstansSecret.Companion.SLACK_COMMON_WEBHOOK_URI
import com.zozo_tech.fcm_example.com.ConstansSecret.Companion.SLACK_WEBHOOK_DEV_CHANNEL
import com.zozo_tech.fcm_example.com.ConstansSecret.Companion.SLACK_WEBHOOK_RELEASE_CHANNEL
import com.zozo_tech.fcm_example.com.Constants.Companion.CLIENT_FUEL
import com.zozo_tech.fcm_example.com.Constants.Companion.CLIENT_RETROFIT2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

class RequestClient(val onSuccess: () -> Unit, val onError: () -> Unit) {

    companion object {
        const val TAG = "RequestClient"
    }

    fun post(client: String, baseUrl: String, resourceUrl: String, body: String) {
        when (client) {
            CLIENT_RETROFIT2 -> postRetrofit2(baseUrl, body)
            CLIENT_FUEL -> postFuel(baseUrl, resourceUrl, body)
            else -> Log.d(TAG, "HTTPクライアントを正しく設定してください")
        }
    }

    data class SlackWebHook(
        val text: String
    )

    interface SlackReleaseService {
        @POST(SLACK_WEBHOOK_RELEASE_CHANNEL)
        fun sendSlackWebHook(@Body slackWebHook: SlackWebHook): Call<String>
    }
    interface SlackDevService {
        @POST(SLACK_WEBHOOK_DEV_CHANNEL)
        fun sendSlackWebHook(@Body slackWebHook: SlackWebHook): Call<String>
    }


    private fun postRetrofit2(baseUrl: String, body: String) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .build()

        // TODO: ここを開発用と本番用のリソース名に切り分ける方法
        val service = retrofit.create(SlackDevService::class.java)
        // val service = retrofit.create(SlackReleaseService::class.java)

        service.sendSlackWebHook(SlackWebHook(body)).enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    when (response.isSuccessful) {
                        true -> onSuccess.invoke() // HTTPリクエストのレスポンスのステータスコードが200番台
                        false -> { // ステータスコードがそれ以外
                            Log.d(TAG, response.errorBody().toString())
                            onError.invoke()
                        }
                    }
                }

                override fun onFailure(call: Call<String>, throwable: Throwable) {
                    Log.d(TAG, throwable.message)
                    onError.invoke()
                }
            }
        )
    }


    private fun postFuel(baseUrl: String, resourceUrl: String, body: String) {
        val adapter = Moshi.Builder()
            .build()
            .adapter(SlackWebHook::class.java)

        Fuel.post(baseUrl + resourceUrl)
            .jsonBody(adapter.toJson(SlackWebHook(body)))
            .responseString { _, _, result ->
                when (result) {
                    is Result.Success -> onSuccess.invoke()
                    is Result.Failure -> {
                        Log.d(TAG, result.error.message)
                        onError.invoke()
                    }
                }
            }

    }
}