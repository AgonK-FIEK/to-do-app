package com.taskmaster.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "password_resets")
data class PasswordResetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val codeHash: String,
    val createdAt: Long = System.currentTimeMillis(),
    val attempts: Int = 0,
    val used: Boolean = false
)