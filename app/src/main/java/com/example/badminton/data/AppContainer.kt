package com.example.badminton.data

import android.content.Context
import com.example.badminton.data.booking.BookingCourtRepository
import com.example.badminton.data.booking.BookingRepository
import com.example.badminton.data.court.CourtRepository
import com.example.badminton.data.payment.PaymentRepository
import com.example.badminton.data.review.ReviewRepository
import com.example.badminton.data.user.UserRepository


interface AppContainer {
    val userRepository: UserRepository
    val courtRepository: CourtRepository
    val bookingRepository: BookingRepository
    val bookingCourtRepository: BookingCourtRepository
    val reviewRepository: ReviewRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val userRepository: UserRepository by lazy {
        UserRepository(AppDatabase.getDatabase(context).userDao())
    }

    override val courtRepository: CourtRepository by lazy{
        CourtRepository(AppDatabase.getDatabase(context).courtDao())
    }

    override val bookingRepository: BookingRepository by lazy {
        BookingRepository(AppDatabase.getDatabase(context).bookingDao())
    }

    override val bookingCourtRepository: BookingCourtRepository by lazy {
        BookingCourtRepository(AppDatabase.getDatabase(context).bookingCourtDao())
    }

    override val reviewRepository: ReviewRepository by lazy {
        ReviewRepository(AppDatabase.getDatabase(context).reviewDao())
    }

}
