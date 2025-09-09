package com.taskmaster.presentation.main.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmaster.data.database.dao.TaskDao
import com.taskmaster.data.database.entity.TaskEntity
import com.taskmaster.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    private val _tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val tasks: StateFlow<List<TaskEntity>> = _tasks
    
    init {
        loadTasks()
    }
    
    private fun loadTasks() {
        viewModelScope.launch {
            val userId = sessionManager.getUserId()
            taskDao.getTasksByUser(userId).collect { taskList ->
                _tasks.value = taskList
            }
        }
    }
    
    fun addTask(title: String, description: String) {
        viewModelScope.launch {
            val task = TaskEntity(
                userId = sessionManager.getUserId(),
                title = title,
                description = description
            )
            taskDao.insertTask(task)
        }
    }
    
    fun updateTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            taskDao.updateTask(task)
        }
    }
    
    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
        }
    }
}
