package com.example.badminton.ui.booking

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.badminton.data.booking.Booking
import com.example.badminton.data.court.Court
import com.example.badminton.ui.AppViewModelProvider
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsScreen(navController: NavController, viewModel: BookingListViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val bookingsWithCourts by viewModel.bookingsWithCourts.collectAsState()
    val pastBookingsWithCourts by viewModel.pastBookingsWithCourts.collectAsState()
    var selectedView by remember { mutableStateOf("Upcoming Bookings") }
    val items = listOf("Upcoming Bookings", "Past Bookings")
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Bookings", style = MaterialTheme.typography.titleLarge) },
            )
        } ,
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { expanded = true })
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium)
                    .padding(8.dp)
            ) {
                Text(
                    text = selectedView,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    items.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item, style = MaterialTheme.typography.bodyMedium) },
                            onClick = {
                                selectedView = item
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (selectedView == "Upcoming Bookings") {
                    if (bookingsWithCourts.isEmpty()) {
                        item {
                            Text(
                                text = "No upcoming bookings",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                            )
                        }
                    } else {
                        items(bookingsWithCourts) { (booking, courts) ->
                            BookingItem(booking = booking, courts = courts, onCancel = { viewModel.cancelBooking(booking) })
                        }
                    }
                } else if (selectedView == "Past Bookings") {
                    if (pastBookingsWithCourts.isEmpty()) {
                        item {
                            Text(
                                text = "No past bookings",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                            )
                        }
                    } else {
                        items(pastBookingsWithCourts) { (booking, courts) ->
                            BookingItem(booking = booking, courts = courts, onCancel = null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingItem(booking: Booking, courts: List<Court>, onCancel: (() -> Unit)?) {
    val totalPrice = courts.sumOf { it.courtPrice }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val bookingDate = dateFormat.format(booking.bookingDate)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Booking ID: ${booking.bookingId}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = "Booking Date: $bookingDate",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Text(
                text = "Start Time: ${booking.startTime}",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Text(
                text = "End Time: ${booking.endTime}",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            courts.forEach { court ->
                Text(
                    text = "Court: ${court.courtName} (${court.courtType})",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Total Price: RM $totalPrice",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            )
            onCancel?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)
                ) {
                    Text("Cancel Booking")
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = false,
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.DateRange, contentDescription = "View Booking") },
            label = { Text("View Booking") },
            selected = true,
            onClick = {  }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Star, contentDescription = "View Review") },
            label = { Text("View Review") },
            selected = false,
            onClick = { navController.navigate("user_review") }
        )
    }
}
