package com.example.badminton.data.booking

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.badminton.data.court.Court

@Entity(
    tableName = "booking_courts",
    primaryKeys = ["bookingId", "courtId"],
    foreignKeys = [
        ForeignKey(entity = Booking::class, parentColumns = ["bookingId"], childColumns = ["bookingId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Court::class, parentColumns = ["courtId"], childColumns = ["courtId"])
    ],
    indices = [Index(value = ["bookingId"]), Index(value = ["courtId"])]
)
data class BookingCourt(
    val bookingId: Int,
    val courtId: Int
)