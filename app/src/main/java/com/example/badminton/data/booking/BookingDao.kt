package com.example.badminton.data.booking

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.badminton.data.court.Court
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface BookingDao {
    @Insert
    suspend fun insertBooking(booking: Booking): Long

    @Query("SELECT * FROM bookings")
    fun getAllBookings(): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE bookingId = :bookingId")
    suspend fun getBookingById(bookingId: Int): Booking?

    @Query("SELECT * FROM bookings WHERE userId = :userId")
    fun getBookingsByUserId(userId: Int): Flow<List<Booking>>

    @Query("""
        SELECT * FROM booking_courts 
        WHERE bookingId IN (
            SELECT bookingId FROM bookings 
            WHERE bookingDate = :date 
            AND ((startTime < :endTime AND endTime > :startTime))
        )
    """)
    suspend fun getBookedCourts(date: Date, startTime: String, endTime: String): List<BookingCourt>

    @Query("""
        SELECT * FROM booking_courts 
        WHERE bookingId IN (
            SELECT bookingId FROM bookings 
            WHERE bookingDate = :date
        )
    """)
    suspend fun getBookedCourtsByDate(date: Date): List<BookingCourt>

    @Query("DELETE FROM bookings WHERE bookingId = :bookingId")
    suspend fun deleteBookingById(bookingId: Int)

    @Query("DELETE FROM booking_courts WHERE bookingId = :bookingId")
    suspend fun deleteBookingCourtsByBookingId(bookingId: Int)

    @Query("""
        SELECT courts.* FROM courts 
        INNER JOIN booking_courts ON courts.courtId = booking_courts.courtId 
        WHERE booking_courts.bookingId = :bookingId
    """)
    suspend fun getCourtsForBooking(bookingId: Int): List<Court>

    @Transaction
    suspend fun deleteBookingAndRelatedCourts(bookingId: Int) {
        deleteBookingById(bookingId)
        deleteBookingCourtsByBookingId(bookingId)
    }

    @Query("SELECT * FROM bookings WHERE bookingDate = :date")
    fun getBookingsByDate(date: Date): Flow<List<Booking>>

    @Query("UPDATE bookings SET bookingStatus = :status WHERE bookingId = :bookingId")
    suspend fun updateBookingStatus(bookingId: Int, status: String)
}
