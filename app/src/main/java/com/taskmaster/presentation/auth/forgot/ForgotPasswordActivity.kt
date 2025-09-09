package com.taskmaster.presentation.auth.forgot

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.taskmaster.databinding.ActivityForgotPasswordBinding
import com.taskmaster.utils.ValidationUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityForgotPasswordBinding
    
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
            Toast.makeText(this, "Password reset email sent to $email", Toast.LENGTH_LONG).show()
            finish()
        }
        
        binding.tvBackToLogin.setOnClickListener {
            finish()
        }
    }
}
