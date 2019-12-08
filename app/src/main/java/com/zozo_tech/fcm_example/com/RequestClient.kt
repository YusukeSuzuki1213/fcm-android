package com.zozo_tech.fcm_example.com

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.awaitResponse
import com.github.kittinunf.fuel.core.awaitResponseResult
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.github.kittinunf.result.Result
import com.squareup.moshi.Moshi
import com.zozo_tech.fcm_example.com.ConstansSecret.Companion.SLACK_WEBHOOK_DEV_CHANNEL
import com.zozo_tech.fcm_example.com.ConstansSecret.Companion.SLACK_WEBHOOK_RELEASE_CHANNEL
import com.zozo_tech.fcm_example.com.Constants.Companion.ASYNC_CALLBACK
import com.zozo_tech.fcm_example.com.Constants.Companion.ASYNC_COROUTINE
import com.zozo_tech.fcm_example.com.Constants.Companion.CLIENT_FUEL
import com.zozo_tech.fcm_example.com.Constants.Companion.CLIENT_RETROFIT2
import kotlinx.coroutines.*
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

    fun post(client: String, asyncMethod: String, baseUrl: String, resourceUrl: String, body: String) {
        when (client) {
            CLIENT_RETROFIT2 ->
                when (asyncMethod) {
                    ASYNC_CALLBACK  -> postRetrofit2WithCallback(baseUrl, resourceUrl, body)
                    ASYNC_COROUTINE -> postRetrofit2WithCoroutine(baseUrl, resourceUrl, body)
                }
            CLIENT_FUEL ->
                when (asyncMethod) {
                    ASYNC_CALLBACK  -> postFuelWithCallback(baseUrl, resourceUrl, body)
                    ASYNC_COROUTINE -> postFuelWithCoroutine(baseUrl, resourceUrl, body)
                }
            else -> Log.d(TAG, "HTTPクライアントを正しく設定してください")
        }
    }

    data class SlackWebHook(
        val text: String
    )

    interface SlackService {
        @POST(SLACK_WEBHOOK_DEV_CHANNEL)
        fun sendSlackWebHookDevCallback(@Body slackWebHook: SlackWebHook): Call<String>

        @POST(SLACK_WEBHOOK_DEV_CHANNEL)
        suspend fun sendSlackWebHookDevCoroutine(@Body slackWebHook: SlackWebHook): Response<String>

        @POST(SLACK_WEBHOOK_RELEASE_CHANNEL)
        fun sendSlackWebHookReleaseCallback(@Body slackWebHook: SlackWebHook): Call<String>

        @POST(SLACK_WEBHOOK_RELEASE_CHANNEL)
        suspend fun sendSlackWebHookReleaseCoroutine(@Body slackWebHook: SlackWebHook): Response<String>
    }

    private fun postRetrofit2WithCallback(baseUrl: String, resourceUrl: String, body: String) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .build()

        val service = retrofit.create(SlackService::class.java)

        val call = when(resourceUrl) {
            SLACK_WEBHOOK_DEV_CHANNEL     -> service.sendSlackWebHookDevCallback(SlackWebHook(body))
            SLACK_WEBHOOK_RELEASE_CHANNEL -> service.sendSlackWebHookReleaseCallback(SlackWebHook(body))
            else -> service.sendSlackWebHookDevCallback(SlackWebHook(body))
        }

        call.enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    when {
                        response.isSuccessful -> onSuccess.invoke() // HTTPリクエストのレスポンスのステータスコードが200番台
                        else -> { // ステータスコードがそれ以外
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

    private fun postRetrofit2WithCoroutine(baseUrl: String, resourceUrl: String, body: String) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .build()

        val service = retrofit.create(SlackService::class.java)
        runBlocking {
            val result = when(resourceUrl) {
                SLACK_WEBHOOK_DEV_CHANNEL     -> service.sendSlackWebHookDevCoroutine(SlackWebHook(body))
                SLACK_WEBHOOK_RELEASE_CHANNEL -> service.sendSlackWebHookReleaseCoroutine(SlackWebHook(body))
                else -> service.sendSlackWebHookDevCoroutine(SlackWebHook(body))
            }
            when {
                result.isSuccessful -> onSuccess.invoke() // HTTPリクエストのレスポンスのステータスコードが200番台
                else -> { // ステータスコードがそれ以外
                    Log.d(TAG, result.errorBody().toString())
                    onError.invoke()
                }
            }
        }
    }

    private fun postFuelWithCallback(baseUrl: String, resourceUrl: String, body: String) {
        val adapter = Moshi.Builder()
            .build()
            .adapter(SlackWebHook::class.java)

        Fuel.post(baseUrl + resourceUrl)
            .jsonBody(adapter.toJson(SlackWebHook(body)))
            .responseString { _, _, result ->
                when (result) {
                    is Result.Success -> onSuccess.invoke() // HTTPリクエストのレスポンスのステータスコードが200番台
                    is Result.Failure -> { // それ以外
                        Log.d(TAG, result.error.message)
                        onError.invoke()
                    }
                }
            }
    }

    private fun postFuelWithCoroutine(baseUrl: String, resourceUrl: String, body: String) {
        val adapter = Moshi.Builder()
            .build()
            .adapter(SlackWebHook::class.java)

        runBlocking {
            Fuel.post(baseUrl + resourceUrl)
                .jsonBody(adapter.toJson(SlackWebHook(body)))
                .awaitStringResponseResult()
                .third // TODO: ここわかりにくくね？
                .fold(
                    { _ -> onSuccess.invoke() }, // HTTPリクエストのレスポンスのステータスコードが200番台
                    { error -> // それ以外
                        Log.d(TAG, error.message)
                        onError.invoke()
                    }
                )
        }
    }
}

