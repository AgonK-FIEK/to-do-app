package com.taskmaster.utils

object ValidationUtils {
    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordValid(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[!@#\$%^&*])(?=.*[a-z])(?=.*[A-Z]).{8,}$"
        return password.matches(passwordPattern.toRegex())
    }

    fun getPasswordError(password: String): String? {
        return when {
            password.length < 8 -> "Password must be at least 8 characters"
            !password.any { it.isDigit() } -> "Password must contain at least 1 number"
            !password.any { it.isUpperCase() } -> "Password must contain at least 1 uppercase letter"
            !password.any { it.isLowerCase() } -> "Password must contain at least 1 lowercase letter"
            !password.contains(Regex("[!@#\$%^&*]")) -> "Password must contain at least 1 special character"
            else -> null
        }
    }
}
