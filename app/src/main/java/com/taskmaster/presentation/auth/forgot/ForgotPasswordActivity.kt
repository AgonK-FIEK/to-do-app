package com.taskmaster.presentation.auth.forgot

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.taskmaster.data.database.dao.PasswordResetDao
import com.taskmaster.data.database.entity.PasswordResetEntity
import com.taskmaster.databinding.ActivityForgotPasswordBinding
import com.taskmaster.presentation.auth.reset.ResetPasswordActivity
import com.taskmaster.utils.EmailUtils
import com.taskmaster.utils.PasswordUtils
import com.taskmaster.utils.ValidationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.security.SecureRandom
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    @Inject
    lateinit var passwordResetDao: PasswordResetDao
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.btnSendEmail.setOnClickListener {
            val email = binding.etEmail.text.toString()

            if (email.isEmpty()) {
                binding.tilEmail.error = "Email is required"
                return@setOnClickListener
            }

            if (!ValidationUtils.isEmailValid(email)) {
                binding.tilEmail.error = "Invalid email format"
                return@setOnClickListener
            }

            binding.tilEmail.error = null
            binding.btnSendEmail.isEnabled = false

            lifecycleScope.launch {
                try {
                    val expiryTime = System.currentTimeMillis() - (10 * 60 * 1000)
                    passwordResetDao.deleteExpiredResets(expiryTime)

                    val resetCode = generateSecureResetCode()
                    val codeHash = PasswordUtils.hashPassword(resetCode)

                    val resetEntity = PasswordResetEntity(
                        email = email,
                        codeHash = codeHash,
                        createdAt = System.currentTimeMillis(),
                        attempts = 0,
                        used = false
                    )
                    passwordResetDao.insertReset(resetEntity)

                    EmailUtils.sendPasswordResetCode(email, resetCode)
                        .onSuccess {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Reset code sent to $email",
                                Toast.LENGTH_LONG
                            ).show()

                            // Only pass email, not the code
                            val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java).apply {
                                putExtra("email", email)
                            }
                            startActivity(intent)
                            finish()
                        }
                        .onFailure {
                            binding.btnSendEmail.isEnabled = true
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Failed to send email. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } catch (e: Exception) {
                    binding.btnSendEmail.isEnabled = true
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        
        binding.tvBackToLogin.setOnClickListener {
            finish()
        }
    }

    private fun generateSecureResetCode(): String {
        val secureRandom = SecureRandom()
        val code = secureRandom.nextInt(900000) + 100000
        return code.toString()
    }
}
