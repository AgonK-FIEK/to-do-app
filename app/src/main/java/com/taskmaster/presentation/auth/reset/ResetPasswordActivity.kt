package com.taskmaster.presentation.auth.reset

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.taskmaster.data.database.dao.PasswordResetDao
import com.taskmaster.data.database.dao.UserDao
import com.taskmaster.databinding.ActivityResetPasswordBinding
import com.taskmaster.presentation.auth.login.LoginActivity
import com.taskmaster.utils.PasswordUtils
import com.taskmaster.utils.ValidationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding

    @Inject
    lateinit var userDao: UserDao

    @Inject
    lateinit var passwordResetDao: PasswordResetDao

    private var email: String? = null
    private val codeExpiryMillis = 10 * 60 * 1000
    private val maxAttempts = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = intent.getStringExtra("email")

        binding.tvEmail.text = "Resetting password for: $email"

        binding.btnReset.setOnClickListener {
            val enteredCode = binding.etCode.text.toString()
            val newPassword = binding.etNewPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (validateInput(enteredCode, newPassword, confirmPassword)) {
                validateAndResetPassword(enteredCode, newPassword)
            }
        }

        binding.tvCancel.setOnClickListener {
            finish()
        }
    }

    private fun validateInput(code: String, password: String, confirmPassword: String): Boolean {
        if (code.isEmpty()) {
            binding.tilCode.error = "Code is required"
            return false
        }

        if (code.length != 6) {
            binding.tilCode.error = "Code must be 6 digits"
            return false
        }

        if (password.isEmpty()) {
            binding.tilNewPassword.error = "Password is required"
            return false
        }

        ValidationUtils.getPasswordError(password)?.let {
            binding.tilNewPassword.error = it
            return false
        }

        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Passwords do not match"
            return false
        }

        binding.tilCode.error = null
        binding.tilNewPassword.error = null
        binding.tilConfirmPassword.error = null
        return true
    }

    private fun validateAndResetPassword(enteredCode: String, newPassword: String) {
        lifecycleScope.launch {
            try {
                email?.let { userEmail ->
                    // Get latest reset from database
                    val resetEntity = passwordResetDao.getLatestReset(userEmail)

                    if (resetEntity == null) {
                        binding.tilCode.error = "No reset request found. Please request a new code."
                        return@launch
                    }

                    if (resetEntity.used) {
                        binding.tilCode.error = "This code has already been used"
                        return@launch
                    }

                    val currentTime = System.currentTimeMillis()
                    if (currentTime - resetEntity.createdAt > codeExpiryMillis) {
                        binding.tilCode.error = "Code has expired"
                        Toast.makeText(this@ResetPasswordActivity, "Code expired. Please request a new one.", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    if (resetEntity.attempts >= maxAttempts) {
                        binding.tilCode.error = "Too many attempts. Please request a new code."
                        Toast.makeText(this@ResetPasswordActivity, "Maximum attempts reached.", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    if (!PasswordUtils.checkPassword(enteredCode, resetEntity.codeHash)) {
                        passwordResetDao.incrementAttempts(resetEntity.id)
                        val remainingAttempts = maxAttempts - (resetEntity.attempts + 1)
                        binding.tilCode.error = "Invalid code. $remainingAttempts attempts remaining"
                        return@launch
                    }

                    // Code is valid - update password
                    val user = userDao.getUserByEmail(userEmail)
                    user?.let {
                        val hashedPassword = PasswordUtils.hashPassword(newPassword)
                        val updatedUser = it.copy(passwordHash = hashedPassword)
                        userDao.updateUser(updatedUser)

                        // Mark reset as used
                        passwordResetDao.markAsUsed(resetEntity.id)

                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "Password reset successful! Please login.",
                            Toast.LENGTH_LONG
                        ).show()

                        val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@ResetPasswordActivity, "Reset failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}