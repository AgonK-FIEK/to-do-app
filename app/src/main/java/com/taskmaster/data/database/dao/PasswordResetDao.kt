package com.taskmaster.data.database.dao

import androidx.room.*
import com.taskmaster.data.database.entity.PasswordResetEntity

@Dao
interface PasswordResetDao {
    @Insert
    suspend fun insertReset(reset: PasswordResetEntity): Long

    @Query("SELECT * FROM password_resets WHERE email = :email AND used = 0 ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestReset(email: String): PasswordResetEntity?

    @Query("UPDATE password_resets SET attempts = attempts + 1 WHERE id = :id")
    suspend fun incrementAttempts(id: Long)

    @Query("UPDATE password_resets SET used = 1 WHERE id = :id")
    suspend fun markAsUsed(id: Long)

    @Query("DELETE FROM password_resets WHERE createdAt < :timestamp")
    suspend fun deleteExpiredResets(timestamp: Long)
}