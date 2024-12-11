package com.example.badminton.data.booking

import kotlinx.coroutines.flow.Flow

class BookingCourtRepository(private val bookingCourtDao: BookingCourtDao) {

    suspend fun insertBookingCourt(bookingCourt: BookingCourt): Long {
        return bookingCourtDao.insertBookingCourt(bookingCourt)
    }

    fun getCourtsForBooking(bookingId: Int): Flow<List<BookingCourt>> {
        return bookingCourtDao.getCourtsForBooking(bookingId)
    }

    fun getBookingsForCourt(courtId: Int): Flow<List<BookingCourt>> {
        return bookingCourtDao.getBookingsForCourt(courtId)
    }
}