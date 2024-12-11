package com.example.badminton.ui.review

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.badminton.data.booking.Booking
import com.example.badminton.data.booking.BookingRepository
import com.example.badminton.data.review.Review
import com.example.badminton.data.review.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class ReviewViewModel(
    private val reviewRepository: ReviewRepository,
    private val bookingRepository: BookingRepository,
    context: Context
) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private fun fetchUserId(): Int? {
        val userId = sharedPreferences.getString("user_id", null)?.toIntOrNull()
        return userId
    }

    val userId = fetchUserId()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> get() = _reviews

    private val _pastBookings = MutableStateFlow<List<Booking>>(emptyList())
    val pastBookings: StateFlow<List<Booking>> get() = _pastBookings

    init {
        fetchUserReviews()
        fetchPastBookings()
    }

    private fun fetchUserReviews() {
        viewModelScope.launch {
            if (userId != null) {
                reviewRepository.getReviewsByUserId(userId).collect { reviewsList ->
                    _reviews.value = reviewsList
                }
            }
        }
    }

    private fun fetchPastBookings() {
        viewModelScope.launch {
            if (userId != null) {
                bookingRepository.getPastBookingsByUserId(userId).collect { pastBookingsList ->
                    _pastBookings.value = pastBookingsList
                }
            }
        }
    }

    fun addReview(bookingId: Int?, courtId: Int?, rating: Int, comment: String) {
        viewModelScope.launch {
            val review = userId?.let {
                Review(
                    userId = it,
                    bookingId = bookingId,
                    courtId = courtId,
                    rating = rating,
                    comment = comment,
                    reviewDate = Date()
                )
            }
            if (review != null) {
                reviewRepository.insertReview(review)
            }
            fetchUserReviews()
        }
    }

    fun deleteReview(review: Review) {
        viewModelScope.launch {
            reviewRepository.deleteReview(review)
            fetchUserReviews()
        }
    }

    fun updateReview(review: Review) {
        viewModelScope.launch {
            reviewRepository.updateReview(review)
            fetchUserReviews()
        }
    }
}
