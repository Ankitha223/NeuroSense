package com.example.neurosense

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

object SensorApi {

    private const val BASE_URL =
        "http://10.103.55.93/esp32/fetch.php"

    private val client = OkHttpClient()

    suspend fun getSensorData(): List<SensorData> {

        return withContext(Dispatchers.IO) {

            val request = Request.Builder()
                .url(BASE_URL)
                .get()
                .build()

            client.newCall(request).execute().use { response ->

                if (!response.isSuccessful) {
                    throw Exception(
                        "Server error: ${response.code}"
                    )
                }

                val responseBody = response.body?.string()
                    ?: throw Exception("Empty response from server")

                val jsonArray = JSONArray(responseBody)

                val sensorList = mutableListOf<SensorData>()

                for (i in 0 until jsonArray.length()) {

                    val jsonObject = jsonArray.getJSONObject(i)

                    val sensorData = SensorData(
                        id = jsonObject.optString("id"),
                        ax = jsonObject.optString("ax"),
                        ay = jsonObject.optString("ay"),
                        az = jsonObject.optString("az"),
                        gx = jsonObject.optString("gx"),
                        gy = jsonObject.optString("gy"),
                        gz = jsonObject.optString("gz"),
                        fsr = jsonObject.optString("fsr"),
                        created_at = jsonObject.optString("created_at")
                    )

                    sensorList.add(sensorData)
                }

                sensorList
            }
        }
    }
}