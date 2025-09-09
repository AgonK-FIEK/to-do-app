package com.taskmaster.presentation.auth.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.taskmaster.databinding.ActivityLoginBinding
import com.taskmaster.presentation.auth.register.RegisterActivity
import com.taskmaster.presentation.auth.forgot.ForgotPasswordActivity
import com.taskmaster.presentation.main.MainActivity
import com.taskmaster.utils.ValidationUtils
import com.taskmaster.utils.NotificationHelper
import com.taskmaster.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    
    @Inject
    lateinit var sessionManager: SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            
            if (validateInput(email, password)) {
                viewModel.login(email, password)
            }
        }
        
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        
        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }
    
    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            return false
        }
        
        if (!ValidationUtils.isEmailValid(email)) {
            binding.tilEmail.error = "Invalid email format"
            return false
        }
        
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            return false
        }
        
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        return true
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Loading -> {
                        binding.btnLogin.isEnabled = false
                    }
                    is LoginState.Success -> {
                        NotificationHelper.showWelcomeNotification(this@LoginActivity, "User")
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                    is LoginState.Error -> {
                        binding.btnLogin.isEnabled = true
                        Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                    is LoginState.Idle -> {
                        binding.btnLogin.isEnabled = true
                    }
                }
            }
        }
    }
}
