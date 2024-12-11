package com.example.badminton.ui.booking

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.badminton.data.booking.Booking
import com.example.badminton.data.booking.BookingCourt
import com.example.badminton.data.booking.BookingCourtRepository
import com.example.badminton.data.booking.BookingRepository
import com.example.badminton.data.court.Court
import com.example.badminton.data.court.CourtRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BookingViewModel(
    private val bookingRepository: BookingRepository,
    private val courtRepository: CourtRepository,
    private val bookingCourtRepository: BookingCourtRepository,
    context: Context
) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _courts = MutableStateFlow<List<Court>>(emptyList())
    val courts: StateFlow<List<Court>> get() = _courts

    private val _selectedDate = MutableStateFlow<Date?>(null)
    val selectedDate: StateFlow<Date?> get() = _selectedDate

    private val _startTime = MutableStateFlow("")
    val startTime: StateFlow<String> get() = _startTime

    private val _duration = MutableStateFlow("")
    val duration: StateFlow<String> get() = _duration

    private val _endTime = MutableStateFlow<String?>(null)
    val endTime: StateFlow<String?> get() = _endTime

    private val _selectedCourts = MutableStateFlow<List<Court>>(emptyList())
    val selectedCourts: StateFlow<List<Court>> get() = _selectedCourts

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> get() = _totalPrice

    private val _courtTotalPrices = MutableStateFlow<Map<Court, Double>>(emptyMap())
    val courtTotalPrices: StateFlow<Map<Court, Double>> get() = _courtTotalPrices

    private val _bookingCompleted = MutableStateFlow(false)
    val bookingCompleted: StateFlow<Boolean> get() = _bookingCompleted

    private val _court = MutableStateFlow<Court?>(null)
    val court: StateFlow<Court?> get() = _court

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private fun fetchUserId(): Int? {
        val userId = sharedPreferences.getString("user_id", null)?.toIntOrNull()
        if (userId != null) {
            Log.d("BookingViewModel", "Successfully accessed SharedPreferences: User ID = $userId")
        } else {
            Log.d("BookingViewModel", "Failed to access SharedPreferences: User ID not found")
        }
        return userId
    }

    val userId = fetchUserId()

    init {
        fetchCourts()
    }

    private fun fetchCourts() {
        viewModelScope.launch {
            try {
                courtRepository.getAllCourts().collect { courtsList ->
                    _courts.value = courtsList
                    Log.d("BookingViewModel", "Successfully fetched all courts")
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Failed to fetch courts", e)
            }
        }
    }

    fun selectDate(date: Date) {
        _selectedDate.value = date
        resetTimeAndDuration()
        resetEndTime()
        updateTotalPrice()
        fetchAvailableCourts()
        Log.d("BookingViewModel", "Selected date: $date")
    }

    fun setStartTime(time: String) {
        _startTime.value = time
        updateEndTime()
        updateTotalPrice()
        fetchAvailableCourts()
        Log.d("BookingViewModel", "Selected start time: $time")
    }

    fun setDuration(duration: String) {
        _duration.value = duration
        updateEndTime()
        updateTotalPrice()
        fetchAvailableCourts()
        Log.d("BookingViewModel", "Selected duration: $duration")
    }

    private fun resetTimeAndDuration() {
        _startTime.value = ""
        _duration.value = ""
        Log.d("BookingViewModel", "Reset start time and duration")
    }

    private fun updateEndTime() {
        val start = _startTime.value
        val dur = _duration.value
        if (start.isNotEmpty() && dur.isNotEmpty()) {
            _endTime.value = calculateEndTime(start, dur)
            Log.d("BookingViewModel", "Updated end time: ${_endTime.value}")
        } else {
            _endTime.value = null
            Log.d("BookingViewModel", "Reset end time")
        }
    }

    private fun resetEndTime() {
        _endTime.value = null
        Log.d("BookingViewModel", "Reset end time")
    }

    fun selectCourt(court: Court) {
        _selectedCourts.value += court
        updateTotalPrice()
        Log.d("BookingViewModel", "Selected court: ${court.courtId}")
    }

    fun deselectCourt(court: Court) {
        _selectedCourts.value -= court
        updateTotalPrice()
        Log.d("BookingViewModel", "Deselected court: ${court.courtId}")
    }

    fun fetchCourtById(courtId: Int) {
        viewModelScope.launch {
            courtRepository.getCourtById(courtId).collect { court ->
                _court.value = court
            }
        }
    }

    private fun calculateEndTime(startTime: String, duration: String): String {
        // Extract the first numeric part from the duration string
        val durationInt = Regex("\\d+").find(duration)?.value?.toIntOrNull()
            ?: throw IllegalArgumentException("Invalid duration format: $duration")

        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = dateFormat.parse(startTime) ?: throw IllegalArgumentException("Invalid start time format: $startTime")

        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.HOUR_OF_DAY, durationInt)
        return dateFormat.format(calendar.time)
    }

    private fun updateTotalPrice() {
        val duration = _duration.value.split(" ").firstOrNull()?.toIntOrNull() ?: 0
        val courtPrices = mutableMapOf<Court, Double>()
        var totalPrice = 0.0

        _selectedCourts.value.forEach { court ->
            val courtTotalPrice = court.courtPrice * duration
            courtPrices[court] = courtTotalPrice
            totalPrice += courtTotalPrice
        }

        _courtTotalPrices.value = courtPrices
        _totalPrice.value = totalPrice
        Log.d("BookingViewModel", "Updated total price: $totalPrice")
    }

    fun resetBookingCompleted() {
        _bookingCompleted.value = false
    }

    private fun fetchAvailableCourts() {
        val date = _selectedDate.value
        val start = _startTime.value
        val duration = _duration.value

        if (date != null && start.isNotEmpty() && duration.isNotEmpty()) {
            val end = calculateEndTime(start, duration)
            viewModelScope.launch {
                try {
                    _isLoading.value = true
                    val bookedCourts = bookingRepository.getBookedCourts(date, start, end)
                    val allCourts = courtRepository.getAllCourts().first()
                    val availableCourts = allCourts.filter { court ->
                        bookedCourts.none { it.courtId == court.courtId }
                    }
                    _courts.value = availableCourts
                    _isLoading.value = false
                    Log.d("BookingViewModel", "Successfully fetched available courts")
                } catch (e: Exception) {
                    _isLoading.value = false
                    _errorMessage.value = e.message
                    Log.e("BookingViewModel", "Failed to fetch available courts", e)
                }
            }
        } else {
            _courts.value = emptyList()
            Log.d("BookingViewModel", "No available courts: Date, start time, or duration not selected")
        }
    }

    fun completeBooking() {
        viewModelScope.launch {
            try {
                val userIdString = sharedPreferences.getString("user_id", null)
                val userId = userIdString?.toIntOrNull()
                if (userId == null || userId == -1) {
                    Log.e("BookingViewModel", "User ID not found or invalid in SharedPreferences")
                    return@launch
                }

                val booking = Booking(
                    userId = userId,
                    bookingDate = _selectedDate.value!!,
                    startTime = _startTime.value,
                    endTime = _endTime.value!!,
                    totalPrice = _totalPrice.value,
                    bookingStatus = "Pending"
                )
                val bookingId = bookingRepository.insertBooking(booking)

                _selectedCourts.value.forEach { court ->
                    val bookingCourt = BookingCourt(
                        bookingId = bookingId.toInt(),
                        courtId = court.courtId
                    )
                    bookingCourtRepository.insertBookingCourt(bookingCourt)
                }
                Log.d("BookingViewModel", "Successfully completed booking: Booking ID = $bookingId")
                _bookingCompleted.value = true
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Failed to complete booking", e)
            }
        }
    }

    fun resetBooking() {
        _selectedDate.value = null
        _startTime.value = ""
        _duration.value = ""
        _endTime.value = null
        _selectedCourts.value = emptyList()
        _totalPrice.value = 0.0
        _courtTotalPrices.value = emptyMap()
        Log.d("BookingViewModel", "Reset booking state")
    }
}
