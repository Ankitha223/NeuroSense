package com.example.neurosense.data

import android.content.Context

class UserStorage(context: Context) {

    private val preferences = context.getSharedPreferences(
        "neurosense_users",
        Context.MODE_PRIVATE
    )

    // --------------------------------------------------
    // SAVE USER
    // --------------------------------------------------

    fun saveUser(
        userId: String,
        name: String,
        age: String,
        gender: String,
        faceImagePath: String
    ) {

        preferences.edit()
            .putString("userId", userId)
            .putString("name", name)
            .putString("age", age)
            .putString("gender", gender)
            .putString("faceImagePath", faceImagePath)
            .apply()
    }

    // --------------------------------------------------
    // GET USER DETAILS
    // --------------------------------------------------

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

    fun hasRegisteredUser(): Boolean {
        return getUserId().isNotEmpty() &&
                getFaceImagePath().isNotEmpty()
    }

    // --------------------------------------------------
    // FACENET EMBEDDING
    // --------------------------------------------------

    fun saveFaceEmbedding(
        embedding: FloatArray
    ) {

        val embeddingString =
            embedding.joinToString(",")

        preferences.edit()
            .putString(
                "face_embedding",
                embeddingString
            )
            .apply()
    }

    fun getFaceEmbedding(): FloatArray? {

        val embeddingString =
            preferences.getString(
                "face_embedding",
                null
            )

        if (embeddingString.isNullOrEmpty()) {
            return null
        }

        return try {

            embeddingString
                .split(",")
                .map { it.toFloat() }
                .toFloatArray()

        } catch (e: Exception) {

            e.printStackTrace()

            null
        }
    }

    fun hasFaceEmbedding(): Boolean {

        val embedding =
            getFaceEmbedding()

        return embedding != null &&
                embedding.size == 128
    }
}