package com.example.badminton.ui.court

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.badminton.data.court.Court
import com.example.badminton.data.timeSlots
import com.example.badminton.ui.AppViewModelProvider
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourtScreen(
    navController: NavController,
    viewModel: CourtViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val courts by viewModel.courts.collectAsState()
    val courtTimeSlotAvailability by viewModel.courtTimeSlotAvailability.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Courts", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Date Picker
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = selectedDate?.let { dateFormat.format(it) } ?: "",
                    onValueChange = {},
                    label = { Text("Select Date") },
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = { showDatePicker = true }),
                    enabled = false,
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = Color.Black,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        disabledBorderColor = Color.Black.copy(alpha = 0.5f),
                        disabledLabelColor = Color.Black.copy(alpha = 0.5f)
                    )
                )
            }

            if (showDatePicker) {
                val context = LocalContext.current
                val datePickerDialog = DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        viewModel.onDateSelected(Date.from(LocalDate.of(year, month + 1, dayOfMonth).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                        showDatePicker = false
                    },
                    selectedDate?.year?.plus(1900) ?: LocalDate.now().year,
                    selectedDate?.month ?: LocalDate.now().monthValue - 1,
                    selectedDate?.date ?: LocalDate.now().dayOfMonth
                )
                datePickerDialog.setOnDismissListener {
                    showDatePicker = false
                }
                datePickerDialog.show()
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedDate != null) {
                LazyColumn {
                    items(courts) { court ->
                        CourtItem(court, courtTimeSlotAvailability[court.courtId] ?: emptyList(), timeSlots, selectedDate, navController)
                    }
                }
            } else {
                Text(
                    text = "Please select a date to view available courts.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CourtItem(
    court: Court,
    availableTimeSlots: List<String>,
    allTimeSlots: List<String>,
    selectedDate: Date?,
    navController: NavController
) {
    val currentTime = Calendar.getInstance().time
    val isToday = selectedDate?.let {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) ==
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentTime)
    } == true

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = court.courtName, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Type: ${court.courtType}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Price: $${court.courtPrice}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Available Time Slots", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(allTimeSlots) { timeSlot ->
                    val timeSlotDate = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(timeSlot)
                    val calendar = Calendar.getInstance()
                    calendar.time = selectedDate ?: currentTime
                    calendar.set(Calendar.HOUR_OF_DAY, timeSlotDate.hours)
                    calendar.set(Calendar.MINUTE, timeSlotDate.minutes)
                    val timeSlotFullDate = calendar.time

                    val isPastTime = isToday && timeSlotFullDate.before(currentTime)

                    Button(
                        onClick = {
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val formattedDate = selectedDate?.let { dateFormat.format(it) } ?: ""
                            navController.navigate("booking_from_court/$formattedDate/${court.courtId}/$timeSlot")
                        },
                        enabled = availableTimeSlots.contains(timeSlot) && !isPastTime,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (availableTimeSlots.contains(timeSlot) && !isPastTime) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            contentColor = if (availableTimeSlots.contains(timeSlot) && !isPastTime) Color.White else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(text = timeSlot, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
