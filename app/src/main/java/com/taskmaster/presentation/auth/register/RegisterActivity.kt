package com.taskmaster.presentation.auth.register

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.taskmaster.databinding.ActivityRegisterBinding
import com.taskmaster.presentation.main.MainActivity
import com.taskmaster.utils.ValidationUtils
import com.taskmaster.utils.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkNotificationPermission()
        setupClickListeners()
        observeViewModel()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                }
                else -> {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            
            if (validateInput(fullName, email, password, confirmPassword)) {
                viewModel.register(fullName, email, password)
            }
        }
        
        binding.tvLogin.setOnClickListener {
            finish()
        }
    }
    
    private fun validateInput(fullName: String, email: String, password: String, confirmPassword: String): Boolean {
        if (fullName.isEmpty()) {
            binding.tilFullName.error = "Full name is required"
            return false
        }
        
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
        
        ValidationUtils.getPasswordError(password)?.let {
            binding.tilPassword.error = it
            return false
        }
        
        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Passwords do not match"
            return false
        }
        
        binding.tilFullName.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null
        return true
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.registerState.collect { state ->
                when (state) {
                    is RegisterState.Loading -> {
                        binding.btnRegister.isEnabled = false
                    }
                    is RegisterState.Success -> {
                        val fullName = binding.etFullName.text.toString()
                        NotificationHelper.showWelcomeNotification(this@RegisterActivity, fullName)
                        Toast.makeText(this@RegisterActivity, "Welcome! Check notifications.", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                        finish()
                    }
                    is RegisterState.Error -> {
                        binding.btnRegister.isEnabled = true
                        Toast.makeText(this@RegisterActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                    is RegisterState.Idle -> {
                        binding.btnRegister.isEnabled = true
                    }
                }
            }
        }
    }
}
