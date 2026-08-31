
package com.example.neurosense.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class UserStorage(context: Context) {

    private val preferences =
        context.getSharedPreferences(
            "neurosense_users",
            Context.MODE_PRIVATE
        )

    companion object {
        private const val USERS_KEY = "users"

        private const val CURRENT_USER_ID = "userId"
        private const val CURRENT_USER_NAME = "name"
        private const val CURRENT_USER_AGE = "age"
        private const val CURRENT_USER_GENDER = "gender"
        private const val CURRENT_USER_FACE_PATH = "faceImagePath"
        private const val CURRENT_USER_EMBEDDING = "face_embedding"
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

        if (userId.isBlank()) {
            return
        }

        val users = getUsersJson()

        val newUser = JSONObject()

        newUser.put(
            "userId",
            userId
        )

        newUser.put(
            "name",
            name
        )

        newUser.put(
            "age",
            age
        )

        newUser.put(
            "gender",
            gender
        )

        newUser.put(
            "faceImagePath",
            faceImagePath
        )

        // --------------------------------------------------
        // PRESERVE EXISTING EMBEDDING
        // --------------------------------------------------

        var userFound = false

        for (i in 0 until users.length()) {

            val existingUser =
                users.optJSONObject(i)
                    ?: continue

            if (
                existingUser.optString(
                    "userId"
                ) == userId
            ) {

                if (
                    existingUser.has(
                        "faceEmbedding"
                    )
                ) {

                    newUser.put(
                        "faceEmbedding",
                        existingUser.getJSONArray(
                            "faceEmbedding"
                        )
                    )
                }

                users.put(
                    i,
                    newUser
                )

                userFound = true

                break
            }
        }

        // --------------------------------------------------
        // ADD NEW USER
        // --------------------------------------------------

        if (!userFound) {

            users.put(
                newUser
            )
        }

        saveUsersJson(
            users
        )

        // --------------------------------------------------
        // SET CURRENT USER
        // --------------------------------------------------

        setCurrentUserDetails(
            userId = userId,
            name = name,
            age = age,
            gender = gender,
            faceImagePath = faceImagePath
        )
    }

    // --------------------------------------------------
    // CURRENT USER DETAILS
    // --------------------------------------------------

    fun getUserId(): String {

        return preferences.getString(
            CURRENT_USER_ID,
            ""
        ) ?: ""
    }

    fun getName(): String {

        return preferences.getString(
            CURRENT_USER_NAME,
            ""
        ) ?: ""
    }

    fun getAge(): String {

        return preferences.getString(
            CURRENT_USER_AGE,
            ""
        ) ?: ""
    }

    fun getGender(): String {

        return preferences.getString(
            CURRENT_USER_GENDER,
            "Select Gender"
        ) ?: "Select Gender"
    }

    fun getFaceImagePath(): String {

        return preferences.getString(
            CURRENT_USER_FACE_PATH,
            ""
        ) ?: ""
    }

    // --------------------------------------------------
    // CHECK REGISTERED USER
    // --------------------------------------------------

    fun hasRegisteredUser(): Boolean {

        return getUserId().isNotBlank()
    }

    // --------------------------------------------------
    // SET CURRENT USER
    // --------------------------------------------------

    fun setCurrentUser(
        user: UserData
    ) {

        setCurrentUserDetails(
            userId = user.userId,
            name = user.name,
            age = user.age,
            gender = user.gender,
            faceImagePath = user.faceImagePath
        )

        // --------------------------------------------------
        // LOAD THAT USER'S EMBEDDING
        // --------------------------------------------------

        if (
            user.faceEmbedding != null &&
            user.faceEmbedding.size == 128
        ) {

            saveCurrentUserEmbedding(
                user.faceEmbedding
            )

        } else {

            preferences.edit()
                .remove(
                    CURRENT_USER_EMBEDDING
                )
                .apply()
        }
    }

    private fun setCurrentUserDetails(
        userId: String,
        name: String,
        age: String,
        gender: String,
        faceImagePath: String
    ) {

        preferences.edit()
            .putString(
                CURRENT_USER_ID,
                userId
            )
            .putString(
                CURRENT_USER_NAME,
                name
            )
            .putString(
                CURRENT_USER_AGE,
                age
            )
            .putString(
                CURRENT_USER_GENDER,
                gender
            )
            .putString(
                CURRENT_USER_FACE_PATH,
                faceImagePath
            )
            .apply()
    }

    // --------------------------------------------------
    // SAVE FACENET EMBEDDING
    // --------------------------------------------------

    fun saveFaceEmbedding(
        embedding: FloatArray
    ) {

        val userId =
            getUserId()

        if (userId.isBlank()) {
            return
        }

        saveFaceEmbeddingForUser(
            userId = userId,
            embedding = embedding
        )

        // --------------------------------------------------
        // SAVE AS CURRENT USER EMBEDDING
        // --------------------------------------------------

        saveCurrentUserEmbedding(
            embedding
        )
    }

    // --------------------------------------------------
    // SAVE EMBEDDING FOR SPECIFIC USER
    // --------------------------------------------------

    fun saveFaceEmbeddingForUser(
        userId: String,
        embedding: FloatArray
    ) {

        if (
            userId.isBlank() ||
            embedding.size != 128
        ) {
            return
        }

        val users =
            getUsersJson()

        for (i in 0 until users.length()) {

            val user =
                users.optJSONObject(i)
                    ?: continue

            if (
                user.optString(
                    "userId"
                ) == userId
            ) {

                user.put(
                    "faceEmbedding",
                    embeddingToJsonArray(
                        embedding
                    )
                )

                users.put(
                    i,
                    user
                )

                break
            }
        }

        saveUsersJson(
            users
        )
    }

    // --------------------------------------------------
    // GET CURRENT USER EMBEDDING
    // --------------------------------------------------

    fun getFaceEmbedding(): FloatArray? {

        val embeddingString =
            preferences.getString(
                CURRENT_USER_EMBEDDING,
                null
            )

        if (
            embeddingString.isNullOrBlank()
        ) {
            return null
        }

        return try {

            val values =
                embeddingString
                    .split(",")

            if (
                values.size != 128
            ) {
                return null
            }

            FloatArray(
                values.size
            ) { index ->

                values[index]
                    .toFloat()
            }

        } catch (e: Exception) {

            e.printStackTrace()

            null
        }
    }

    // --------------------------------------------------
    // CHECK CURRENT USER EMBEDDING
    // --------------------------------------------------

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

        val users =
            getUsersJson()

        val result =
            mutableListOf<UserData>()

        for (i in 0 until users.length()) {

            try {

                val user =
                    users.getJSONObject(i)

                val embedding =
                    jsonArrayToEmbedding(
                        user.optJSONArray(
                            "faceEmbedding"
                        )
                    )

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
                                "gender",
                                "Select Gender"
                            ),

                        faceImagePath =
                            user.optString(
                                "faceImagePath"
                            ),

                        faceEmbedding =
                            embedding
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

        if (userId.isBlank()) {
            return null
        }

        return getAllUsers()
            .find { user ->
                user.userId == userId
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

        if (
            userId.isBlank() ||
            embedding.size != 128
        ) {
            return
        }

        // --------------------------------------------------
        // UPDATE USER DETAILS
        // --------------------------------------------------

        saveUser(
            userId = userId,
            name = name,
            age = age,
            gender = gender,
            faceImagePath = faceImagePath
        )

        // --------------------------------------------------
        // UPDATE EMBEDDING
        // --------------------------------------------------

        saveFaceEmbeddingForUser(
            userId = userId,
            embedding = embedding
        )

        // --------------------------------------------------
        // MAKE THIS USER CURRENT
        // --------------------------------------------------

        setCurrentUserDetails(
            userId = userId,
            name = name,
            age = age,
            gender = gender,
            faceImagePath = faceImagePath
        )

        saveCurrentUserEmbedding(
            embedding
        )
    }

    // --------------------------------------------------
    // CLEAR CURRENT USER
    // --------------------------------------------------

    fun clearCurrentUser() {

        preferences.edit()
            .remove(CURRENT_USER_ID)
            .remove(CURRENT_USER_NAME)
            .remove(CURRENT_USER_AGE)
            .remove(CURRENT_USER_GENDER)
            .remove(CURRENT_USER_FACE_PATH)
            .remove(CURRENT_USER_EMBEDDING)
            .apply()
    }

    // --------------------------------------------------
    // SAVE CURRENT USER EMBEDDING
    // --------------------------------------------------

    private fun saveCurrentUserEmbedding(
        embedding: FloatArray
    ) {

        if (embedding.size != 128) {
            return
        }

        val embeddingString =
            embedding.joinToString(",")

        preferences.edit()
            .putString(
                CURRENT_USER_EMBEDDING,
                embeddingString
            )
            .apply()
    }

    // --------------------------------------------------
    // GET USERS JSON
    // --------------------------------------------------

    private fun getUsersJson(): JSONArray {

        val jsonString =
            preferences.getString(
                USERS_KEY,
                "[]"
            ) ?: "[]"

        return try {

            JSONArray(
                jsonString
            )

        } catch (e: Exception) {

            e.printStackTrace()

            JSONArray()
        }
    }

    // --------------------------------------------------
    // SAVE USERS JSON
    // --------------------------------------------------

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

    // --------------------------------------------------
    // FLOAT ARRAY → JSON ARRAY
    // --------------------------------------------------

    private fun embeddingToJsonArray(
        embedding: FloatArray
    ): JSONArray {

        val array =
            JSONArray()

        for (value in embedding) {

            array.put(
                value.toDouble()
            )
        }

        return array
    }

    // --------------------------------------------------
    // JSON ARRAY → FLOAT ARRAY
    // --------------------------------------------------

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

                array
                    .getDouble(index)
                    .toFloat()
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

