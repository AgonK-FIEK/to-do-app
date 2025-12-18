package com.taskmaster.presentation.auth.twofa

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.taskmaster.databinding.ActivityTwoFaBinding
import com.taskmaster.presentation.main.MainActivity
import com.taskmaster.utils.EmailUtils
import com.taskmaster.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TwoFAActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTwoFaBinding

    @Inject
    lateinit var sessionManager: SessionManager

    private var correctCode: String? = null
    private var userEmail: String? = null
    private var userId: Long = -1
    private var codeGeneratedAt: Long = 0
    private var attemptCount = 0
    private val maxAttempts = 5
    private val codeExpiryMillis = 5 * 60 * 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTwoFaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        correctCode = intent.getStringExtra("code")
        userEmail = intent.getStringExtra("email")
        userId = intent.getLongExtra("userId", -1)
        codeGeneratedAt = System.currentTimeMillis()

        binding.tvEmail.text = "Verification code sent to: $userEmail"

        binding.btnVerify.setOnClickListener {
            val enteredCode = binding.etCode.text.toString()

            if (enteredCode.isEmpty()) {
                binding.tilCode.error = "Code is required"
                return@setOnClickListener
            }

            if (attemptCount >= maxAttempts) {
                Toast.makeText(this, "Too many attempts. Please request a new code.", Toast.LENGTH_SHORT).show()
                finish()
                return@setOnClickListener
            }

            val currentTime = System.currentTimeMillis()
            if (currentTime - codeGeneratedAt > codeExpiryMillis) {
                binding.tilCode.error = "Code expired"
                Toast.makeText(this, "Code has expired. Please request a new code.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (enteredCode == correctCode) {
                sessionManager.saveUserSession(userId, userEmail ?: "")
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            } else {
                attemptCount++
                val remainingAttempts = maxAttempts - attemptCount
                binding.tilCode.error = "Invalid code. $remainingAttempts attempts remaining"
                Toast.makeText(this, "Invalid code. $remainingAttempts attempts remaining", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvResend.setOnClickListener {
            binding.tvResend.isEnabled = false

            lifecycleScope.launch {
                val newCode = generate2FACode()
                correctCode = newCode
                codeGeneratedAt = System.currentTimeMillis()
                attemptCount = 0

                EmailUtils.send2FACode(userEmail ?: "", newCode)
                    .onSuccess {
                        Toast.makeText(this@TwoFAActivity, "New code sent to $userEmail", Toast.LENGTH_SHORT).show()
                    }
                    .onFailure {
                        Toast.makeText(this@TwoFAActivity, "Failed to send code. Please try again.", Toast.LENGTH_SHORT).show()
                    }

                delay(30000)
                binding.tvResend.isEnabled = true
            }
        }

        binding.tvCancel.setOnClickListener {
            finish()
        }
    }

    private fun generate2FACode(): String {
        return (100000..999999).random().toString()
    }
}