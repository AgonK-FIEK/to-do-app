package com.taskmaster.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val passwordHash: String,
    val fullName: String,
    val createdAt: Long = System.currentTimeMillis(),
    val twoFactorEnabled: Boolean = false
)