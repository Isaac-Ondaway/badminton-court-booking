package com.example.badminton.data.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.badminton.data.user.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE userID = :userId")
    suspend fun findUserById(userId: Int): User?

}