package com.taskmaster.presentation.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmaster.data.database.dao.TaskDao
import com.taskmaster.data.database.dao.UserDao
import com.taskmaster.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    private val _dashboardState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val dashboardState: StateFlow<DashboardState> = _dashboardState
    
    fun loadDashboard() {
        viewModelScope.launch {
            val userId = sessionManager.getUserId()
            val user = userDao.getUserById(userId)
            
            taskDao.getTasksByUser(userId).collect { tasks ->
                val totalTasks = tasks.size
                val completedTasks = tasks.count { it.isCompleted }
                val pendingTasks = totalTasks - completedTasks
                
                _dashboardState.value = DashboardState.Success(
                    userName = user?.fullName ?: "",
                    totalTasks = totalTasks,
                    completedTasks = completedTasks,
                    pendingTasks = pendingTasks
                )
            }
        }
    }
}

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(
        val userName: String,
        val totalTasks: Int,
        val completedTasks: Int,
        val pendingTasks: Int
    ) : DashboardState()
}
