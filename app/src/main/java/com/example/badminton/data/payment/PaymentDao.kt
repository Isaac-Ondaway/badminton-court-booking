package com.example.badminton.data.payment

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: Payment)

    @Query("SELECT * FROM payments WHERE bookingId = :bookingId")
    suspend fun getPaymentsForBooking(bookingId: Int): List<Payment>
}
