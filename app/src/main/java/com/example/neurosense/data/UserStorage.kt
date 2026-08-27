package com.example.neurosense.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class UserStorage(context: Context) {

    private val preferences = context.getSharedPreferences(
        "neurosense_users",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val USERS_KEY = "users"
    }

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

        val users = getUsersJson()

        val user = JSONObject()

        user.put("userId", userId)
        user.put("name", name)
        user.put("age", age)
        user.put("gender", gender)
        user.put("faceImagePath", faceImagePath)

        // Replace existing user if same ID exists
        var replaced = false

        for (i in 0 until users.length()) {

            val existingUser = users.getJSONObject(i)

            if (existingUser.getString("userId") == userId) {

                // Preserve existing face embedding
                if (existingUser.has("faceEmbedding")) {

                    user.put(
                        "faceEmbedding",
                        existingUser.getJSONArray("faceEmbedding")
                    )
                }

                users.put(i, user)

                replaced = true

                break
            }
        }

        if (!replaced) {
            users.put(user)
        }

        saveUsersJson(users)

        // --------------------------------------------------
        // SET CURRENT USER
        // --------------------------------------------------

        preferences.edit()
            .putString("userId", userId)
            .putString("name", name)
            .putString("age", age)
            .putString("gender", gender)
            .putString("faceImagePath", faceImagePath)
            .apply()
    }

    // --------------------------------------------------
    // GET CURRENT USER DETAILS
    // --------------------------------------------------

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

    fun hasRegisteredUser(): Boolean {

        return getUserId().isNotEmpty() &&
                getFaceImagePath().isNotEmpty()
    }

    // --------------------------------------------------
    // SET CURRENT USER
    // --------------------------------------------------

    fun setCurrentUser(
        user: UserData
    ) {

        preferences.edit()
            .putString(
                "userId",
                user.userId
            )
            .putString(
                "name",
                user.name
            )
            .putString(
                "age",
                user.age
            )
            .putString(
                "gender",
                user.gender
            )
            .putString(
                "faceImagePath",
                user.faceImagePath
            )
            .apply()
    }

    // --------------------------------------------------
    // FACENET EMBEDDING
    // --------------------------------------------------

    fun saveFaceEmbedding(
        embedding: FloatArray
    ) {

        val userId = getUserId()

        if (userId.isEmpty()) {
            return
        }

        val users = getUsersJson()

        for (i in 0 until users.length()) {

            val user = users.getJSONObject(i)

            if (user.getString("userId") == userId) {

                user.put(
                    "faceEmbedding",
                    embeddingToJsonArray(embedding)
                )

                users.put(
                    i,
                    user
                )

                break
            }
        }

        saveUsersJson(users)

        // --------------------------------------------------
        // OLD EMBEDDING KEY
        // Keep this for compatibility
        // --------------------------------------------------

        val embeddingString =
            embedding.joinToString(",")

        preferences.edit()
            .putString(
                "face_embedding",
                embeddingString
            )
            .apply()
    }

    // --------------------------------------------------
    // GET CURRENT USER EMBEDDING
    // --------------------------------------------------

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
                .map {
                    it.toFloat()
                }
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

    // --------------------------------------------------
    // GET ALL USERS
    // --------------------------------------------------

    fun getAllUsers(): List<UserData> {

        val users = getUsersJson()

        val result =
            mutableListOf<UserData>()

        for (i in 0 until users.length()) {

            try {

                val user =
                    users.getJSONObject(i)

                result.add(
                    UserData(

                        userId =
                            user.optString(
                                "userId"
                            ),

                        name =
                            user.optString(
                                "name"
                            ),

                        age =
                            user.optString(
                                "age"
                            ),

                        gender =
                            user.optString(
                                "gender"
                            ),

                        faceImagePath =
                            user.optString(
                                "faceImagePath"
                            ),

                        faceEmbedding =
                            jsonArrayToEmbedding(
                                user.optJSONArray(
                                    "faceEmbedding"
                                )
                            )
                    )
                )

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }

        return result
    }

    // --------------------------------------------------
    // GET USER BY ID
    // --------------------------------------------------

    fun getUserById(
        userId: String
    ): UserData? {

        return getAllUsers()
            .find {
                it.userId == userId
            }
    }

    // --------------------------------------------------
    // UPDATE EXISTING USER
    // --------------------------------------------------

    fun updateUser(
        userId: String,
        name: String,
        age: String,
        gender: String,
        faceImagePath: String,
        embedding: FloatArray
    ) {

        saveUser(
            userId = userId,
            name = name,
            age = age,
            gender = gender,
            faceImagePath = faceImagePath
        )

        // Make this user the current user
        preferences.edit()
            .putString(
                "userId",
                userId
            )
            .apply()

        saveFaceEmbedding(
            embedding
        )
    }

    // --------------------------------------------------
    // PRIVATE JSON FUNCTIONS
    // --------------------------------------------------

    private fun getUsersJson(): JSONArray {

        val jsonString =
            preferences.getString(
                USERS_KEY,
                "[]"
            )

        return try {

            JSONArray(
                jsonString
            )

        } catch (e: Exception) {

            JSONArray()
        }
    }

    private fun saveUsersJson(
        users: JSONArray
    ) {

        preferences.edit()
            .putString(
                USERS_KEY,
                users.toString()
            )
            .apply()
    }

    private fun embeddingToJsonArray(
        embedding: FloatArray
    ): JSONArray {

        val array =
            JSONArray()

        for (value in embedding) {

            array.put(
                value
            )
        }

        return array
    }

    private fun jsonArrayToEmbedding(
        array: JSONArray?
    ): FloatArray? {

        if (array == null) {
            return null
        }

        if (array.length() != 128) {
            return null
        }

        return try {

            FloatArray(
                array.length()
            ) { index ->

                array.getDouble(
                    index
                ).toFloat()
            }

        } catch (e: Exception) {

            e.printStackTrace()

            null
        }
    }
}

// --------------------------------------------------
// USER DATA
// --------------------------------------------------

data class UserData(

    val userId: String,

    val name: String,

    val age: String,

    val gender: String,

    val faceImagePath: String,

    val faceEmbedding: FloatArray?
)