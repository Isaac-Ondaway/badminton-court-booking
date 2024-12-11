package com.example.badminton.data.booking


import com.example.badminton.data.court.Court
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.Date

class BookingRepository(private val bookingDao: BookingDao) {

    suspend fun insertBooking(booking: Booking): Long {
        return bookingDao.insertBooking(booking)
    }

    fun getAllBookings(): Flow<List<Booking>> {
        return bookingDao.getAllBookings()
    }

    suspend fun getBookingById(bookingId: Int): Booking? {
        return bookingDao.getBookingById(bookingId)
    }

    fun getBookingsByUserId(userId: Int): Flow<List<Booking>> {
        return bookingDao.getBookingsByUserId(userId)
    }

    suspend fun getBookedCourts(date: Date, startTime: String, endTime: String): List<BookingCourt> {
        return bookingDao.getBookedCourts(date, startTime, endTime)
    }
    suspend fun getBookedCourtsByDate(date: Date): List<BookingCourt> {
        return bookingDao.getBookedCourtsByDate(date)
    }

    fun getPastBookingsByUserId(userId: Int): Flow<List<Booking>> = flow {
        val currentDate = Date()
        val bookings = bookingDao.getBookingsByUserId(userId).first().filter { it.bookingDate.before(currentDate) }
        emit(bookings)
    }

    fun getUpcomingBookingsByUserId(userId: Int): Flow<List<Booking>> = flow {
        val currentDate = Date()
        val bookings = bookingDao.getBookingsByUserId(userId).first().filter { it.bookingDate.after(currentDate) }
        emit(bookings)
    }

    suspend fun getCourtsForBooking(bookingId: Int): List<Court> {
        return bookingDao.getCourtsForBooking(bookingId)
    }

    suspend fun deleteBookingAndRelatedCourts(bookingId: Int) {
        bookingDao.deleteBookingAndRelatedCourts(bookingId)
    }

    fun getBookingsByDate(date: Date): Flow<List<Booking>> {
        return bookingDao.getBookingsByDate(date)
    }

    suspend fun updateBookingStatus(bookingId: Int, status: String) {
        bookingDao.updateBookingStatus(bookingId, status)
    }
}
