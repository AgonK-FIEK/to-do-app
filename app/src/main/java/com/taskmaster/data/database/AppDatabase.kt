package com.taskmaster.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.taskmaster.data.database.dao.UserDao
import com.taskmaster.data.database.dao.TaskDao
import com.taskmaster.data.database.dao.PasswordResetDao
import com.taskmaster.data.database.entity.UserEntity
import com.taskmaster.data.database.entity.TaskEntity
import com.taskmaster.data.database.entity.PasswordResetEntity

@Database(
    entities = [UserEntity::class, TaskEntity::class, PasswordResetEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun passwordResetDao(): PasswordResetDao
}
