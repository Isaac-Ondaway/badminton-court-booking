package com.example.badminton.ui.booking

import android.app.DatePickerDialog
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.badminton.R
import com.example.badminton.data.durations
import com.example.badminton.data.timeSlots
import com.example.badminton.ui.theme.Typography
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    navController: NavController,
    viewModel: BookingViewModel,
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val startTime by viewModel.startTime.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val endTime by viewModel.endTime.collectAsState()
    val availableCourts by viewModel.courts.collectAsState()
    val selectedCourts by viewModel.selectedCourts.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var expandedStart by remember { mutableStateOf(false) }
    var expandedDuration by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book a Badminton Court", style = Typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Book a Badminton Court Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.5f)
                    .verticalScroll(rememberScrollState())
                    .padding(6.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Date Picker
                    OutlinedTextField(
                        value = selectedDate?.let { dateFormat.format(it) } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Date") },
                        modifier = Modifier
                            .fillMaxWidth()
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

                    if (showDatePicker) {
                        DatePickerDialog(
                            initialDate = LocalDate.now(),
                            onDateSelected = { date ->
                                viewModel.selectDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                                showDatePicker = false
                            },
                            onDismissRequest = { showDatePicker = false }
                        )
                    }

                    // Start Time Slot Picker
                    Column {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = {},
                            label = { Text("Select Start Time") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedStart = true },
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
                        DropdownMenu(
                            expanded = expandedStart,
                            onDismissRequest = { expandedStart = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            timeSlots.forEach { time ->
                                DropdownMenuItem(
                                    text = { Text(text = time) },
                                    onClick = {
                                        viewModel.setStartTime(time)
                                        expandedStart = false
                                    }
                                )
                            }
                        }
                    }

                    // Duration Picker
                    Column {
                        OutlinedTextField(
                            value = duration,
                            onValueChange = {},
                            label = { Text("Select Duration") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedDuration = true },
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
                        DropdownMenu(
                            expanded = expandedDuration,
                            onDismissRequest = { expandedDuration = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            durations.forEach { dur ->
                                DropdownMenuItem(
                                    text = { Text(text = dur) },
                                    onClick = {
                                        viewModel.setDuration(dur)
                                        expandedDuration = false
                                    }
                                )
                            }
                        }
                    }

                    // Display End Time
                    endTime?.let {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Text(
                                text = "End Time: $it",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }

            // Available Courts Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.4f)
                    .padding(8.dp),
                contentAlignment = Alignment.TopStart
            ) {
                if (selectedDate != null && startTime.isNotEmpty() && duration.isNotEmpty()) {
                    Column {
                        Text(
                            text = "Available Courts",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(16.dp)
                        )

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(350.dp) // Fixed height for the available courts section
                        ) {
                            items(availableCourts) { court ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (selectedCourts.contains(court)) {
                                                viewModel.deselectCourt(court)
                                            } else {
                                                viewModel.selectCourt(court)
                                            }
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (selectedCourts.contains(court)) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.surface
                                        }
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(text = "Court ${court.courtName}", style = MaterialTheme.typography.bodyLarge)
                                            Text(text = "Type: ${court.courtType}", style = MaterialTheme.typography.bodyMedium)
                                            Text(text = "Price: ${court.courtPrice}", style = MaterialTheme.typography.bodyMedium)
                                        }
                                        Icon(
                                            imageVector = if (selectedCourts.contains(court)) Icons.Filled.CheckCircle else Icons.Filled.CheckCircle,
                                            contentDescription = null,
                                            tint = if (selectedCourts.contains(court)) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Confirm Button Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f)
                    .padding(4.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                if (selectedDate != null && startTime.isNotEmpty() && duration.isNotEmpty() && selectedCourts.isNotEmpty()) {
                    Button(
                        onClick = { navController.navigate("confirmation") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Text("Confirm", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
            },
            initialDate.year,
            initialDate.monthValue - 1,
            initialDate.dayOfMonth
        ).apply {
            setOnDismissListener {
                onDismissRequest()
            }
        }
    }

    DisposableEffect(key1 = true) {
        datePickerDialog.show()
        onDispose { datePickerDialog.dismiss() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ConfirmationScreen(
    navController: NavController,
    viewModel: BookingViewModel
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val startTime by viewModel.startTime.collectAsState()
    val endTime by viewModel.endTime.collectAsState()
    val selectedCourts by viewModel.selectedCourts.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()
    val courtTotalPrices by viewModel.courtTotalPrices.collectAsState()
    val bookingCompleted by viewModel.bookingCompleted.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) }

    if (bookingCompleted) {
        navController.navigate("success") {
            popUpTo("success") { inclusive = true }
        }
        viewModel.resetBookingCompleted()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Summary") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Total Price: RM $totalPrice",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Button(
                        onClick = {
                            navController.navigateUp()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back")
                    }
                    Button(
                        onClick = {
                            viewModel.completeBooking()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Complete Booking")
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(selectedCourts) { court ->
                val courtTotalPrice = courtTotalPrices[court] ?: 0.0

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.elevatedCardElevation()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Court Type",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = court.courtType,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "Date",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = selectedDate?.let { dateFormat.format(it) } ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "Start Time",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = startTime,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "End Time",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = endTime ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "Court",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = court.courtName,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "Total Price for Court",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "RM $courtTotalPrice",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }
}




@Composable
fun SuccessScreen(
    navController: NavController,
    viewModel: BookingViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_confirmation), // Use your own image resource
            contentDescription = "Confirmation Image",
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Thank You!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your booking has been successfully completed.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            viewModel.resetBooking()
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
        }) {
            Text("Ok")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreenCourt(
    navController: NavController,
    viewModel: BookingViewModel,
    date: String,
    courtId: Int,
    timeSlot: String
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val startTime by viewModel.startTime.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val endTime by viewModel.endTime.collectAsState()
    val availableCourts by viewModel.courts.collectAsState()
    val selectedCourts by viewModel.selectedCourts.collectAsState()
    val court by viewModel.court.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var expandedStart by remember { mutableStateOf(false) }
    var expandedDuration by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    BackHandler {
        // Reset booking data when navigating back from the booking screen
        viewModel.resetBooking()
        navController.popBackStack()
    }

    LaunchedEffect(Unit) {
        val parsedDate = dateFormat.parse(date)
        viewModel.fetchCourtById(courtId)
        if (parsedDate != null) {
            viewModel.selectDate(parsedDate)
            viewModel.setStartTime(timeSlot)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book a Badminton Court", style = Typography.titleLarge) },
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Book a Badminton Court Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.5f)
                    .verticalScroll(rememberScrollState())
                    .padding(6.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Date Picker
                    OutlinedTextField(
                        value = selectedDate?.let { dateFormat.format(it) } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Date") },
                        modifier = Modifier
                            .fillMaxWidth()
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

                    if (showDatePicker) {
                        DatePickerDialog(
                            initialDate = LocalDate.now(),
                            onDateSelected = { date ->
                                viewModel.selectDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                                showDatePicker = false
                            },
                            onDismissRequest = { showDatePicker = false }
                        )
                    }

                    // Start Time Slot Picker
                    Column {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = {},
                            label = { Text("Select Start Time") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedStart = true },
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
                        DropdownMenu(
                            expanded = expandedStart,
                            onDismissRequest = { expandedStart = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            timeSlots.forEach { time ->
                                DropdownMenuItem(
                                    text = { Text(text = time) },
                                    onClick = {
                                        viewModel.setStartTime(time)
                                        expandedStart = false
                                    }
                                )
                            }
                        }
                    }

                    // Duration Picker
                    Column {
                        OutlinedTextField(
                            value = duration,
                            onValueChange = {},
                            label = { Text("Select Duration") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedDuration = true },
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
                        DropdownMenu(
                            expanded = expandedDuration,
                            onDismissRequest = { expandedDuration = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            durations.forEach { dur ->
                                DropdownMenuItem(
                                    text = { Text(text = dur) },
                                    onClick = {
                                        viewModel.setDuration(dur)
                                        court?.let { viewModel.selectCourt(it) }
                                        expandedDuration = false
                                    }
                                )
                            }
                        }
                    }

                    // Display End Time
                    endTime?.let {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Text(
                                text = "End Time: $it",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }

            // Available Courts Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.4f)
                    .padding(8.dp),
                contentAlignment = Alignment.TopStart
            ) {
                if (selectedDate != null && startTime.isNotEmpty() && duration.isNotEmpty()) {
                    Column {
                        Text(
                            text = "Available Courts",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(16.dp)
                        )

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(350.dp) // Fixed height for the available courts section
                        ) {
                            items(availableCourts) { court ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (selectedCourts.contains(court)) {
                                                viewModel.deselectCourt(court)
                                            } else {
                                                viewModel.selectCourt(court)
                                            }
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (selectedCourts.contains(court)) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.surface
                                        }
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(text = "Court ${court.courtName}", style = MaterialTheme.typography.bodyLarge)
                                            Text(text = "Type: ${court.courtType}", style = MaterialTheme.typography.bodyMedium)
                                            Text(text = "Price: ${court.courtPrice}", style = MaterialTheme.typography.bodyMedium)
                                        }
                                        Icon(
                                            imageVector = if (selectedCourts.contains(court)) Icons.Filled.CheckCircle else Icons.Filled.CheckCircle,
                                            contentDescription = null,
                                            tint = if (selectedCourts.contains(court)) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Confirm Button Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f)
                    .padding(4.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                if (selectedDate != null && startTime.isNotEmpty() && duration.isNotEmpty() && selectedCourts.isNotEmpty()) {
                    Button(
                        onClick = { navController.navigate("confirmation") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Text("Confirm", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}







