package com.taskmaster.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("TaskMasterPrefs", Context.MODE_PRIVATE)

    fun saveUserId(userId: Long) {
        prefs.edit().putLong("user_id", userId).apply()
    }

    fun getUserId(): Long = prefs.getLong("user_id", -1)

    fun isLoggedIn(): Boolean = getUserId() != -1L

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun saveUserEmail(email: String) {
        prefs.edit().putString("user_email", email).apply()
    }

    fun getUserEmail(): String? = prefs.getString("user_email", null)
}
