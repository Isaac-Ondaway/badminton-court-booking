package com.example.badminton.ui.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.badminton.R
import com.example.badminton.data.booking.Booking
import com.example.badminton.data.court.Court
import com.example.badminton.ui.AppViewModelProvider
import com.example.badminton.ui.auth.UserViewModel
import com.example.badminton.ui.booking.BookingViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel(factory = AppViewModelProvider.Factory),
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val userId = userViewModel.getUserId()
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var navigateToLogin by remember { mutableStateOf(false) }
    val upcomingBookings by homeViewModel.upcomingBookings.collectAsState()
    val currentDateBookings by homeViewModel.currentDateBookings.collectAsState()

    LaunchedEffect(userId) {
        Log.d("HomeScreen", "LaunchedEffect triggered with userId: $userId")
        if (userId == null) {
            Log.d("HomeScreen", "Navigating to login because userId is null")
            navigateToLogin = true
        } else {
            val user = userViewModel.getUserDetails(userId.toInt())
            userName = user?.userName ?: ""
            userEmail = user?.email ?: ""
            Log.d("HomeScreen", "User details fetched: userName=$userName, userEmail=$userEmail")
            homeViewModel.fetchUpcomingBookings()
            homeViewModel.fetchCurrentDateBookings()
        }
    }

    if (navigateToLogin) {
        LaunchedEffect(navigateToLogin) {
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_logo),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CourtBook")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        userViewModel.logout()
                        navigateToLogin = true
                    }) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        if (userId != null && !navigateToLogin) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome, $userName!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Email: $userEmail",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionCard(icon = R.drawable.ic_court, text = "Court") {
                        navController.navigate("court")
                    }
                    ActionCard(icon = R.drawable.ic_booking, text = "Booking") {
                        navController.navigate("booking")
                    }
                    ActionCard(icon = R.drawable.ic_review, text = "Review") {
                        navController.navigate("review")
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                BookingOverviewCard(upcomingBookings)
                Spacer(modifier = Modifier.height(32.dp))
                CurrentDateBookingCard(currentDateBookings)
            }
        } else if (!navigateToLogin) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun BookingOverviewCard(upcomingBookings: List<Booking>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp) // Set a fixed height for the card
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Upcoming Booking",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (upcomingBookings.isNotEmpty()) {
                    BookingTable(upcomingBookings)
                } else {
                    Text(
                        text = "No upcoming bookings",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun CurrentDateBookingCard(currentDateBookings: List<Pair<Booking, Court>>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp) // Set a fixed height for the card
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Today's Bookings",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (currentDateBookings.isNotEmpty()) {
                    CurrentDateBookingTable(currentDateBookings)
                } else {
                    Text(
                        text = "No bookings for today",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun BookingTable(upcomingBookings: List<Booking>) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp)
        ) {
            Text(text = "Date", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(text = "Start Time", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(text = "End Time", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        }
        LazyColumn {
            items(upcomingBookings) { booking ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val bookingDate = dateFormat.format(booking.bookingDate)
                    Text(text = bookingDate, modifier = Modifier.weight(1f))
                    Text(text = booking.startTime, modifier = Modifier.weight(1f))
                    Text(text = booking.endTime, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun CurrentDateBookingTable(currentDateBookings: List<Pair<Booking, Court>>) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp)
        ) {
            Text(text = "Time", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(text = "Court", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        }
        LazyColumn {
            items(currentDateBookings) { (booking, court) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = "${booking.startTime} - ${booking.endTime}", modifier = Modifier.weight(1f))
                    Text(text = court.courtName, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ActionCard(icon: Int, text: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .size(120.dp)
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = true,
            onClick = { /* Handle navigation */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.DateRange, contentDescription = "View Booking") },
            label = { Text("View Booking") },
            selected = false,
            onClick = { navController.navigate("bookings") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Star, contentDescription = "View Review") },
            label = { Text("View Review") },
            selected = false,
            onClick = { navController.navigate("user_review") }
        )
    }
}
