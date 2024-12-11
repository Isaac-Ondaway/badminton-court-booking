package com.example.badminton.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.badminton.BadmintonApplication
import com.example.badminton.ui.auth.UserViewModel
import com.example.badminton.ui.booking.BookingListViewModel
import com.example.badminton.ui.booking.BookingViewModel
import com.example.badminton.ui.court.CourtViewModel
import com.example.badminton.ui.review.ReviewViewModel
import com.example.badminton.ui.home.HomeViewModel


object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for UserViewModel
        initializer {
            val application = badmintonApplication()
            UserViewModel(
                application.container.userRepository,
                application.applicationContext
            )
        }
   
        initializer {
            val application = badmintonApplication()
            CourtViewModel(
                application.container.courtRepository,
                application.container.bookingRepository

            )
        }

        initializer {
            val application = badmintonApplication()
            BookingViewModel(
                application.container.bookingRepository,
                application.container.courtRepository,
                application.container.bookingCourtRepository,
                application.applicationContext
            )
        }

        initializer {
            val application = badmintonApplication()
            BookingListViewModel(
                application.container.bookingRepository,
                application.container.bookingCourtRepository,
                application.applicationContext
            )
        }

        initializer {
            val application = badmintonApplication()
            HomeViewModel(
                application.container.courtRepository,
                application.container.bookingRepository,
                application.applicationContext
            )
        }

        initializer {
            val application = badmintonApplication()
            ReviewViewModel(
                application.container.reviewRepository,
                application.container.bookingRepository,
                application.applicationContext
            )
        }
    }
}

fun CreationExtras.badmintonApplication(): BadmintonApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as BadmintonApplication)
