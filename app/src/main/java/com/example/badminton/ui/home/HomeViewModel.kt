package com.example.badminton.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.badminton.data.booking.Booking
import com.example.badminton.data.booking.BookingRepository
import com.example.badminton.data.court.Court
import com.example.badminton.data.court.CourtRepository
import com.example.badminton.ui.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeViewModel(
    private val courtRepository: CourtRepository,
    private val bookingRepository: BookingRepository,
    context: Context
) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _courts = MutableStateFlow<List<Court>>(emptyList())
    val courts: StateFlow<List<Court>> get() = _courts

    private val _upcomingBookings = MutableStateFlow<List<Booking>>(emptyList())
    val upcomingBookings: StateFlow<List<Booking>> get() = _upcomingBookings

    private val _currentDateBookings = MutableStateFlow<List<Pair<Booking, Court>>>(emptyList())
    val currentDateBookings: StateFlow<List<Pair<Booking, Court>>> get() = _currentDateBookings

    init {
        viewModelScope.launch {
            SessionManager.isLoggedIn.collect { isLoggedIn ->
                if (!isLoggedIn) {
                    resetData()
                }
            }
        }
    }

    private fun resetData() {
        _courts.value = emptyList()
        _upcomingBookings.value = emptyList()
    }


    private fun fetchUserId(): Int? {
        val userId = sharedPreferences.getString("user_id", null)?.toIntOrNull()
        if (userId != null) {
            Log.d("HomeViewModel", "Successfully accessed SharedPreferences: User ID = $userId")
        } else {
            Log.d("HomeViewModel", "Failed to access SharedPreferences: User ID not found")
        }
        return userId
    }

    val userId = fetchUserId()

    fun fetchUpcomingBookings() {
        viewModelScope.launch {
            val currentTime = Calendar.getInstance().time
            val upcomingBookings = mutableListOf<Booking>()

            if (userId != null) {
                val userBookings = bookingRepository.getBookingsByUserId(userId).first()
                userBookings.forEach { booking ->
                    if (booking.bookingDate.after(currentTime)) {
                        upcomingBookings.add(booking)
                    }
                }
            }
            _upcomingBookings.value = upcomingBookings.sortedBy { it.bookingDate }
        }
    }

    fun fetchCurrentDateBookings() {
        viewModelScope.launch {
            val currentTime = Calendar.getInstance().time
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentTime)
            val currentBookings = mutableListOf<Pair<Booking, Court>>()

            if (userId != null) {
                val userBookings = bookingRepository.getBookingsByUserId(userId).first()
                userBookings.forEach { booking ->
                    val bookingDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(booking.bookingDate)
                    if (bookingDate == currentDate) {
                        val courts = bookingRepository.getCourtsForBooking(booking.bookingId)
                        courts.forEach { court ->
                            currentBookings.add(booking to court)
                        }
                    }
                }
            }
            _currentDateBookings.value = currentBookings
        }
    }
}
