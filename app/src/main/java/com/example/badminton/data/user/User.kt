package com.example.badminton.data.user

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User (
    @PrimaryKey(autoGenerate = true) val userID: Int = 0,
    val userName: String,
    val email: String,
    val password: String
)
