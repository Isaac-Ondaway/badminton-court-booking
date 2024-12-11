package com.example.badminton.data.booking

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.badminton.data.Converters
import com.example.badminton.data.court.Court
import com.example.badminton.data.user.User
import java.util.Date

@Entity(
    tableName = "bookings",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["userID"], childColumns = ["userId"]),
    ],
    indices = [Index(value = ["userId"])]
)
@TypeConverters(Converters::class)
data class Booking(
    @PrimaryKey(autoGenerate = true) val bookingId: Int = 0,
    val userId: Int, // Foreign key to User table
    val bookingDate: Date,
    val startTime: String,
    val endTime: String,
    val totalPrice: Double,
    val bookingStatus: String
)