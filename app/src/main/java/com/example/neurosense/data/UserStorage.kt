package com.example.neurosense.data

import android.content.Context

class UserStorage(context: Context) {

    private val preferences = context.getSharedPreferences(
        "neurosense_users",
        Context.MODE_PRIVATE
    )

    // ==================================================
    // SAVE USER
    // ==================================================

    fun saveUser(
        userId: String,
        name: String,
        age: String,
        gender: String,
        faceImagePath: String,
        faceEmbedding: FloatArray
    ) {

        val embeddingString =
            faceEmbedding.joinToString(",")

        preferences.edit()
            .putString("userId", userId)
            .putString("name", name)
            .putString("age", age)
            .putString("gender", gender)
            .putString("faceImagePath", faceImagePath)
            .putString("face_embedding", embeddingString)
            .apply()
    }

    // ==================================================
    // USER DETAILS
    // ==================================================

    fun getUserId(): String {

        return preferences.getString(
            "userId",
            ""
        ) ?: ""
    }

    fun getName(): String {

        return preferences.getString(
            "name",
            ""
        ) ?: ""
    }

    fun getAge(): String {

        return preferences.getString(
            "age",
            ""
        ) ?: ""
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

    // ==================================================
    // GET FACENET EMBEDDING
    // ==================================================

    fun getFaceEmbedding(): FloatArray {

        val embeddingString =
            preferences.getString(
                "face_embedding",
                null
            )

        if (embeddingString.isNullOrEmpty()) {
            return FloatArray(0)
        }

        return try {

            embeddingString
                .split(",")
                .map { it.toFloat() }
                .toFloatArray()

        } catch (e: Exception) {

            e.printStackTrace()

            FloatArray(0)
        }
    }

    // ==================================================
    // CHECK USER
    // ==================================================

    fun hasRegisteredUser(): Boolean {

        return getUserId().isNotEmpty() &&
                getFaceImagePath().isNotEmpty()
    }

    // ==================================================
    // CHECK FACENET
    // ==================================================

    fun hasFaceEmbedding(): Boolean {

        val embedding =
            getFaceEmbedding()

        return embedding != null &&
                embedding.size == 128
    }

    // ==================================================
    // CLEAR USER
    // ==================================================

    fun clearUser() {

        preferences.edit()
            .clear()
            .apply()
    }
}