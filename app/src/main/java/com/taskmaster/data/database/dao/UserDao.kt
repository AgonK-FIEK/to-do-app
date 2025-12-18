package com.taskmaster.data.database.dao

import androidx.room.*
import com.taskmaster.data.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email AND passwordHash = :passwordHash")
    suspend fun login(email: String, passwordHash: String): UserEntity?

    @Query("UPDATE users SET twoFactorEnabled = :enabled WHERE id = :userId")
    suspend fun updateTwoFactorEnabled(userId: Long, enabled: Boolean)
}
