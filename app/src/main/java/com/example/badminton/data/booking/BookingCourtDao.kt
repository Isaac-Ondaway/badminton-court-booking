package com.example.badminton.data.booking

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingCourtDao {
    @Insert
    suspend fun insertBookingCourt(bookingCourt: BookingCourt): Long

    @Query("SELECT * FROM booking_courts WHERE bookingId = :bookingId")
    fun getCourtsForBooking(bookingId: Int): Flow<List<BookingCourt>>

    @Query("SELECT * FROM booking_courts WHERE courtId = :courtId")
    fun getBookingsForCourt(courtId: Int): Flow<List<BookingCourt>>
}