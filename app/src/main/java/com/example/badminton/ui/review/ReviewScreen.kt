package com.example.badminton.ui.review

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.badminton.data.booking.Booking
import com.example.badminton.data.review.Review
import com.example.badminton.ui.AppViewModelProvider
import com.example.badminton.ui.home.BottomNavigationBar
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReviewScreen(
    navController: NavController,
    viewModel: ReviewViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val pastBookings by viewModel.pastBookings.collectAsState()
    val reviews by viewModel.reviews.collectAsState()

    var selectedBooking by remember { mutableStateOf<Booking?>(null) }
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Review", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f) // Ensure the LazyColumn fills available space
                ) {
                    items(pastBookings) { booking ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedBooking = booking
                                    showDialog = true
                                },
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Booking ID: ${booking.bookingId}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Date: ${SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(booking.bookingDate)}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Start Time: ${booking.startTime}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "End Time: ${booking.endTime}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                if (showDialog && selectedBooking != null) {
                    AlertDialog(
                        onDismissRequest = {
                            showDialog = false
                        },
                        title = {
                            Text(text = "Add Review for Booking ID: ${selectedBooking?.bookingId}")
                        },
                        text = {
                            Column {
                                TextField(
                                    value = comment,
                                    onValueChange = { comment = it },
                                    label = { Text("Comment") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = TextFieldDefaults.textFieldColors(
                                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        containerColor = MaterialTheme.colorScheme.surface
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Rating:",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Slider(
                                        value = rating.toFloat(),
                                        onValueChange = { rating = it.toInt() },
                                        valueRange = 0f..5f,
                                        steps = 4,
                                        colors = SliderDefaults.colors(
                                            thumbColor = MaterialTheme.colorScheme.primary,
                                            activeTrackColor = MaterialTheme.colorScheme.primary,
                                            inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.addReview(bookingId = selectedBooking!!.bookingId, courtId = null, rating = rating, comment = comment)
                                    showDialog = false
                                    selectedBooking = null
                                    rating = 0
                                    comment = ""
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Submit Review")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    showDialog = false
                                    selectedBooking = null
                                    rating = 0
                                    comment = ""
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    )
}




@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserReviewScreen(
    navController: NavController,
    viewModel: ReviewViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val reviews by viewModel.reviews.collectAsState()

    var editingReview by remember { mutableStateOf<Review?>(null) }
    var reviewToDelete by remember { mutableStateOf<Review?>(null) }
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "User Reviews", style = MaterialTheme.typography.titleLarge) },
            )
        },
        bottomBar = {
            BottomNavigationBarReview(navController)
        }

    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)) {
            if (reviews.isEmpty()) {
                Text(
                    text = "No reviews available",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(reviews) { review ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { editingReview = review },
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Booking ID: ${review.bookingId}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Court ID: ${review.courtId}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Rating: ${review.rating}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = review.comment,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Date: ${SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(review.reviewDate)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Row {
                                    Button(
                                        onClick = {
                                            editingReview = review
                                            rating = review.rating
                                            comment = review.comment
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Text("Edit Review")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { reviewToDelete = review },
                                        enabled = editingReview == null,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary,
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Text("Delete Review")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            editingReview?.let { review ->
                Dialog(onDismissRequest = { editingReview = null }) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Edit Review for Booking ID: ${review.bookingId}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            TextField(
                                value = comment,
                                onValueChange = { comment = it },
                                label = { Text("Comment") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = TextFieldDefaults.textFieldColors(
                                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Rating:",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Slider(
                                    value = rating.toFloat(),
                                    onValueChange = { rating = it.toInt() },
                                    valueRange = 0f..5f,
                                    steps = 4,
                                    colors = SliderDefaults.colors(
                                        thumbColor = MaterialTheme.colorScheme.primary,
                                        activeTrackColor = MaterialTheme.colorScheme.primary,
                                        inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val updatedReview = review.copy(rating = rating, comment = comment)
                                    viewModel.updateReview(updatedReview)
                                    editingReview = null
                                    rating = 0
                                    comment = ""
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Update Review")
                            }
                        }
                    }
                }
            }

            reviewToDelete?.let { review ->
                AlertDialog(
                    onDismissRequest = { reviewToDelete = null },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.deleteReview(review)
                                reviewToDelete = null
                            }
                        ) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { reviewToDelete = null }) {
                            Text("No")
                        }
                    },
                    title = { Text("Delete Review") },
                    text = { Text("Are you sure you want to delete this review?") }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBarReview(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = false,
            onClick = {  navController.navigate("home") }
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
            selected = true,
            onClick = {  }
        )
    }
}






