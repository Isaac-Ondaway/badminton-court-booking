package com.example.badminton.data.review

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.badminton.data.booking.Booking
import com.example.badminton.data.court.Court
import com.example.badminton.data.user.User
import java.util.Date

@Entity(
    tableName = "reviews",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["userID"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Booking::class, parentColumns = ["bookingId"], childColumns = ["bookingId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Court::class, parentColumns = ["courtId"], childColumns = ["courtId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [
        Index(value = ["bookingId"]),
        Index(value = ["courtId"]),
        Index(value = ["userId"])
    ]
)
data class Review(
    @PrimaryKey(autoGenerate = true) val reviewId: Int = 0,
    val userId: Int,
    val bookingId: Int?,
    val courtId: Int?,
    val rating: Int,
    val comment: String,
    val reviewDate: Date
)

