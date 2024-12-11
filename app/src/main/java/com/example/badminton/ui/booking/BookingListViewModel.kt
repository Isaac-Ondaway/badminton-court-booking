package com.example.badminton.ui.booking

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.badminton.data.booking.Booking
import com.example.badminton.data.booking.BookingCourtRepository
import com.example.badminton.data.booking.BookingRepository
import com.example.badminton.data.court.Court
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookingListViewModel(
    private val bookingRepository: BookingRepository,
    private val bookingCourtRepository: BookingCourtRepository,
    context: Context
) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _bookingsWithCourts = MutableStateFlow<List<Pair<Booking, List<Court>>>>(emptyList())
    val bookingsWithCourts: StateFlow<List<Pair<Booking, List<Court>>>> get() = _bookingsWithCourts

    private val _pastBookingsWithCourts = MutableStateFlow<List<Pair<Booking, List<Court>>>>(emptyList())
    val pastBookingsWithCourts: StateFlow<List<Pair<Booking, List<Court>>>> get() = _pastBookingsWithCourts

    private fun fetchUserId(): Int? {
        val userId = sharedPreferences.getString("user_id", null)?.toIntOrNull()
        return userId
    }

    val userId = fetchUserId()

    init {
        fetchUpcomingBookingsWithCourts()
        fetchPastBookingsWithCourts()
    }

    private fun fetchUpcomingBookingsWithCourts() {
        viewModelScope.launch {
            userId?.let {
                bookingRepository.getUpcomingBookingsByUserId(it).collect { bookingsList ->
                    val bookingsWithCourts = bookingsList.map { booking ->
                        val courts = bookingRepository.getCourtsForBooking(booking.bookingId)
                        booking to courts
                    }.sortedBy { it.first.bookingDate }
                    _bookingsWithCourts.value = bookingsWithCourts
                }
            }
        }
    }

    private fun fetchPastBookingsWithCourts() {
        viewModelScope.launch {
            userId?.let {
                bookingRepository.getPastBookingsByUserId(it).collect { pastBookingsList ->
                    val pastBookingsWithCourts = pastBookingsList.map { booking ->
                        val courts = bookingRepository.getCourtsForBooking(booking.bookingId)
                        booking to courts
                    }.sortedBy { it.first.bookingDate }
                    _pastBookingsWithCourts.value = pastBookingsWithCourts
                }
            }
        }
    }

    fun cancelBooking(booking: Booking) {
        viewModelScope.launch {
            try {
                Log.d("BookingHistoryViewModel", "Cancelling booking: ${booking.bookingId}")
                bookingRepository.deleteBookingAndRelatedCourts(booking.bookingId)
                fetchUpcomingBookingsWithCourts()
                fetchPastBookingsWithCourts()
                Log.d("BookingHistoryViewModel", "Booking cancelled: ${booking.bookingId}")
            } catch (e: Exception) {
                Log.e("BookingHistoryViewModel", "Failed to cancel booking", e)
            }
        }
    }
}
