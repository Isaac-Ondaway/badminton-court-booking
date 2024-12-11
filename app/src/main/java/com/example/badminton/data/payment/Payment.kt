package com.example.badminton.data.payment

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.badminton.data.Converters
import java.util.Date

@Entity(tableName = "payments")
@TypeConverters(Converters::class)
data class Payment(
    @PrimaryKey(autoGenerate = true) val paymentId: Int = 0,
    val bookingId: Int,
    val transactionId: String,
    val paymentDate: Date,
    val amount: Double,
    val status: String
)
