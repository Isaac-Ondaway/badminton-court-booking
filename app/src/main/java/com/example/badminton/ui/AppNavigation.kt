package com.example.badminton.ui

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.badminton.ui.auth.RegisterScreen
import com.example.badminton.ui.home.HomeScreen
import com.example.badminton.ui.auth.LoginScreen
import com.example.badminton.ui.booking.BookingScreen
import com.example.badminton.ui.booking.BookingScreenCourt
import com.example.badminton.ui.booking.BookingViewModel
import com.example.badminton.ui.booking.BookingsScreen
import com.example.badminton.ui.booking.ConfirmationScreen
import com.example.badminton.ui.booking.SuccessScreen
import com.example.badminton.ui.court.CourtScreen
import com.example.badminton.ui.review.ReviewScreen
import com.example.badminton.ui.review.UserReviewScreen

@SuppressLint("SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val bookingViewModel: BookingViewModel = viewModel(factory = AppViewModelProvider.Factory)
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("register") {
            RegisterScreen(navController)
        }

        composable("login") {
            LoginScreen(navController)
        }

        composable("home") {
            HomeScreen(navController)
        }

        composable("court") {
            CourtScreen(navController)
        }


        composable("confirmation") {
            ConfirmationScreen(navController, bookingViewModel)
        }

        composable("success") {
            SuccessScreen(navController, bookingViewModel)
        }

        composable("bookings") {
            BookingsScreen(navController)
        }

        composable("booking") {
            BookingScreen(navController, bookingViewModel)
        }

        composable("review") {
            ReviewScreen(navController)
        }

        composable("user_review") {
            UserReviewScreen(navController)
        }

        composable(
            route = "booking_from_court/{date}/{courtId}/{timeSlot}",
            arguments = listOf(
                navArgument("date") { type = NavType.StringType },
                navArgument("courtId") { type = NavType.IntType },
                navArgument("timeSlot") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date")
            val courtId = backStackEntry.arguments?.getInt("courtId")
            val timeSlot = backStackEntry.arguments?.getString("timeSlot")
            if (date != null) {
                if (timeSlot != null) {
                    if (courtId != null) {
                        BookingScreenCourt(navController, bookingViewModel, date, courtId, timeSlot)
                    }
                }
            }
        }
    }

    BackHandler {
        if (navController.currentBackStackEntry?.destination?.route == "confirmation") {
            navController.popBackStack()
        } else {
            bookingViewModel.resetBooking()
            navController.popBackStack()
        }
    }
}
