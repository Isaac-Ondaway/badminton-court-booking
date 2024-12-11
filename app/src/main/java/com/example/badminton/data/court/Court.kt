package com.example.badminton.data.court

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courts")
data class Court(
    @PrimaryKey(autoGenerate = true) val courtId: Int = 0,
    val courtName: String,
    val courtType: String,
    val courtPrice: Double
)