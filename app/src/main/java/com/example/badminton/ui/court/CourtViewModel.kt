package com.example.badminton.ui.court

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.badminton.data.booking.BookingRepository
import com.example.badminton.data.court.Court
import com.example.badminton.data.court.CourtRepository
import com.example.badminton.data.timeSlots
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CourtViewModel(
    private val courtRepository: CourtRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _courts = MutableStateFlow<List<Court>>(emptyList())
    val courts: StateFlow<List<Court>> get() = _courts

    private val _courtTimeSlotAvailability = MutableStateFlow<Map<Int, List<String>>>(emptyMap())
    val courtTimeSlotAvailability: StateFlow<Map<Int, List<String>>> get() = _courtTimeSlotAvailability

    private val _selectedDate = MutableStateFlow<Date?>(null)
    val selectedDate: StateFlow<Date?> get() = _selectedDate

    init {
        fetchCourts()
    }

    private fun fetchCourts() {
        viewModelScope.launch {
            courtRepository.getAllCourts().collect { courtsList ->
                _courts.value = courtsList
            }
        }
    }

    fun fetchAvailableCourts(date: Date?) {
        viewModelScope.launch {
            val allCourts = courtRepository.getAllCourts().first()
            val currentTime = Calendar.getInstance().time

            val availability = allCourts.associate { court ->
                val bookedTimeSlots = mutableListOf<String>()
                if (date != null) {
                    val bookedCourts = bookingRepository.getBookedCourtsByDate(date)
                    bookedCourts.filter { it.courtId == court.courtId }.forEach { bookingCourt ->
                        val booking = bookingRepository.getBookingById(bookingCourt.bookingId)
                        if (booking != null) {
                            val startTime = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(booking.startTime)
                            val endTime = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(booking.endTime)
                            if (startTime != null && endTime != null) {
                                val calendar = Calendar.getInstance()
                                calendar.time = startTime
                                while (calendar.time.before(endTime)) {
                                    bookedTimeSlots.add(SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time))
                                    calendar.add(Calendar.HOUR_OF_DAY, 1)
                                }
                            }
                        }
                    }
                }

                val availableSlots = timeSlots.filter { timeSlot ->
                    val timeSlotDate = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(timeSlot)
                    val calendar = Calendar.getInstance()
                    calendar.time = date ?: currentTime
                    calendar.set(Calendar.HOUR_OF_DAY, timeSlotDate.hours)
                    calendar.set(Calendar.MINUTE, timeSlotDate.minutes)
                    val timeSlotFullDate = calendar.time

                    val isPastTime = timeSlotFullDate.before(currentTime)
                    val isBooked = bookedTimeSlots.contains(timeSlot)

                    Log.d("CourtViewModel", "Court ID: ${court.courtId}, Date: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(timeSlotFullDate)}, Is Past Time: $isPastTime, Is Booked: $isBooked")
                    !isPastTime && !isBooked
                }
                court.courtId to availableSlots
            }
            _courtTimeSlotAvailability.value = availability
        }
    }

    fun onDateSelected(date: Date?) {
        _selectedDate.value = date
        fetchAvailableCourts(date)
    }
}
