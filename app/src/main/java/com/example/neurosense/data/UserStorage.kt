package com.example.neurosense.data

import android.content.Context
import org.json.JSONArray

class UserStorage(context: Context) {

    private val preferences = context.getSharedPreferences(
        "neurosense_users",
        Context.MODE_PRIVATE
    )

    fun saveUser(
        userId: String,
        name: String,
        age: String,
        gender: String,
        faceImagePath: String,
        faceEmbedding: FloatArray
    ) {

        // Convert FloatArray to JSON string
        val embeddingJson = JSONArray().apply {

            faceEmbedding.forEach { value ->
                put(value.toDouble())
            }

        }.toString()

        preferences.edit()
            .putString("userId", userId)
            .putString("name", name)
            .putString("age", age)
            .putString("gender", gender)
            .putString("faceImagePath", faceImagePath)
            .putString("faceEmbedding", embeddingJson)
            .apply()
    }

    fun getUserId(): String {
        return preferences.getString("userId", "") ?: ""
    }

    fun getName(): String {
        return preferences.getString("name", "") ?: ""
    }

    fun getAge(): String {
        return preferences.getString("age", "") ?: ""
    }

    fun getGender(): String {
        return preferences.getString(
            "gender",
            "Select Gender"
        ) ?: "Select Gender"
    }

    fun getFaceImagePath(): String {
        return preferences.getString(
            "faceImagePath",
            ""
        ) ?: ""
    }

    fun getFaceEmbedding(): FloatArray {

        val embeddingJson =
            preferences.getString(
                "faceEmbedding",
                null
            ) ?: return FloatArray(0)

        return try {

            val jsonArray =
                JSONArray(embeddingJson)

            FloatArray(jsonArray.length()) { index ->
                jsonArray
                    .getDouble(index)
                    .toFloat()
            }

        } catch (e: Exception) {

            e.printStackTrace()

            FloatArray(0)
        }
    }

    fun hasRegisteredUser(): Boolean {

        return getUserId().isNotEmpty() &&
                getFaceImagePath().isNotEmpty() &&
                getFaceEmbedding().size == 128
    }
}