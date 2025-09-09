package com.taskmaster.presentation.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.taskmaster.databinding.ActivitySplashBinding
import com.taskmaster.presentation.auth.login.LoginActivity
import com.taskmaster.presentation.main.MainActivity
import com.taskmaster.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    
    @Inject
    lateinit var sessionManager: SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        animateLogo()
        
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 2000)
    }
    
    private fun animateLogo() {
        binding.ivLogo.alpha = 0f
        binding.ivLogo.animate()
            .alpha(1f)
            .setDuration(1000)
            .setListener(null)
        
        binding.tvAppName.translationY = 50f
        binding.tvAppName.alpha = 0f
        binding.tvAppName.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(500)
            .setListener(null)
    }
    
    private fun navigateToNextScreen() {
        val intent = if (sessionManager.isLoggedIn()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
