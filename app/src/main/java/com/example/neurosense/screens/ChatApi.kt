
package com.example.neurosense

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object ChatApi {

    private val client = OkHttpClient()

    // Android Phone -> Windows computer
    private const val BASE_URL = "http://10.150.33.93:8000"

    suspend fun sendMessage(message: String): String {

        return withContext(Dispatchers.IO) {

            try {

                val json = JSONObject()
                json.put("message", message)

                val requestBody =
                    json.toString()
                        .toRequestBody(
                            "application/json".toMediaType()
                        )

                val request =
                    Request.Builder()
                        .url("$BASE_URL/chat")
                        .post(requestBody)
                        .build()

                client.newCall(request).execute().use { response ->

                    if (!response.isSuccessful) {
                        return@withContext "Sorry, I couldn't connect to the Health Assistant."
                    }

                    val responseBody =
                        response.body?.string()

                    if (responseBody.isNullOrEmpty()) {
                        return@withContext "Sorry, I received an empty response."
                    }

                    val responseJson =
                        JSONObject(responseBody)

                    responseJson.getString("reply")
                }

            } catch (e: Exception) {

                "Unable to connect to the NeuroSense Health Assistant. Please make sure the backend server is running."
            }
        }
    }
}

