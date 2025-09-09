package com.taskmaster.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmaster.data.database.dao.UserDao
import com.taskmaster.data.database.entity.UserEntity
import com.taskmaster.utils.PasswordUtils
import com.taskmaster.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState
    
    fun register(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            
            try {
                val existingUser = userDao.getUserByEmail(email)
                
                if (existingUser != null) {
                    _registerState.value = RegisterState.Error("Email already registered")
                    return@launch
                }
                
                val hashedPassword = PasswordUtils.hashPassword(password)
                val user = UserEntity(
                    email = email,
                    passwordHash = hashedPassword,
                    fullName = fullName
                )
                
                val userId = userDao.insertUser(user)
                sessionManager.saveUserId(userId)
                sessionManager.saveUserEmail(email)
                _registerState.value = RegisterState.Success
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(e.message ?: "Registration failed")
            }
        }
    }
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}
