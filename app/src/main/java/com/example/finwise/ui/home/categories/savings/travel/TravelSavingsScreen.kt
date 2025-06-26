package com.example.finwise.ui.home.categories.savings.travel

import BottomNavigationBar
import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit // Import Edit icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.finwise.R
import com.example.finwise.data.model.savings.Savings
import com.example.finwise.data.model.goal.Goal
import com.example.finwise.ui.components.CentreTopBar
import com.example.finwise.util.Routes
import com.example.finwise.util.UiEvent
import com.example.finwise.util.Utils
import kotlinx.coroutines.flow.collectLatest
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.filled.DateRange
import com.example.finwise.ui.components.SavingsTransactionCard

@SuppressLint("DefaultLocale")
@Composable
fun TravelSavingsScreen(
    navController: NavHostController,
    travelSavingsViewModel: TravelViewModel = hiltViewModel()
) {
    val uiState by travelSavingsViewModel.travelSavingsState.collectAsStateWithLifecycle()

    var showSetGoalDialog by remember { mutableStateOf(false) }
    var newGoalAmountText by remember { mutableStateOf("") }


    // Colors from the screenshot (approximate)
    val PrimaryGreen = Color(0xFF20c997)
    val LightBlueCircle = Color(0xFFE3F2FD) // Background of the circular progress
    val DarkBlueProgress = Color(0xFF0068FF) // Progress part of the circular progress

    val animatedProgressForCircle by animateFloatAsState(
        targetValue = uiState.progressPercentage,
        animationSpec = tween(durationMillis = 1000),
        label = "progressCircleAnimation"
    )
    val animatedProgressForBar by animateFloatAsState(
        targetValue = uiState.progressPercentage / 100f,
        animationSpec = tween(durationMillis = 1000),
        label = "progressBarAnimation"
    )

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        travelSavingsViewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.Navigate -> navController.navigate(event.route)
                is UiEvent.PopBackStack -> navController.popBackStack()
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message, event.action)
            }
        }
    }

    if (showSetGoalDialog) {
        AlertDialog(
            onDismissRequest = { showSetGoalDialog = false },
            title = { Text("Set Travel Goal") },
            text = {
                OutlinedTextField(
                    value = newGoalAmountText,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() || it == '.' }) {
                            newGoalAmountText = newValue
                        }
                    },
                    label = { Text("Enter Goal Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    val amount = newGoalAmountText.toDoubleOrNull()
                    if (amount != null && amount > 0) {
                        travelSavingsViewModel.setTravelGoal(amount)
                        showSetGoalDialog = false
                        newGoalAmountText = ""
                    } else {
                        travelSavingsViewModel.sendUiEvent(UiEvent.ShowSnackbar("Please enter a valid amount."))
                    }
                }) {
                    Text("Set Goal")
                }
            },
            dismissButton = {
                Button(onClick = { showSetGoalDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CentreTopBar(title = "Travel Savings", navController = navController)
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        floatingActionButton = {
            Button(
                onClick = {

                    travelSavingsViewModel.sendUiEvent(UiEvent.Navigate(Routes.AddSavingsScreen))
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // FAB text always "Add Savings" now. Initial goal setting is via header.
                Text(text = "Add Savings", color = Color.White)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFf0faf5)) // Light background color for the screen
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Section (Green Background)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryGreen) // Primary Green from screenshot
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Goal, Progress Circle, Amount Saved Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp), // Inner padding for this row
                        horizontalArrangement = Arrangement.SpaceBetween, // Pushes items to ends
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Column: Goal and Amount Saved
                        Column(
                            horizontalAlignment = Alignment.Start, // Align text to the start within this column
                            modifier = Modifier.weight(1f) // Give it weight to take available space
                        ) {
                            // Goal Section
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.img_35), // Assuming you have a specific goal icon or use checkmark
                                    contentDescription = "Goal Icon",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Goal", style = MaterialTheme.typography.titleSmall, color = Color.White.copy(alpha = 0.8f))
                                Spacer(Modifier.width(4.dp))
                                IconButton(
                                    onClick = {
                                        uiState.travelGoal?.targetAmount?.let {
                                            newGoalAmountText = String.format("%.2f", it)
                                        } ?: run {
                                            newGoalAmountText = ""
                                        }
                                        showSetGoalDialog = true
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Goal",
                                        tint = Color.White
                                    )
                                }
                            }
                            Text(
                                text = "$${String.format("%.2f", uiState.travelGoal?.targetAmount ?: 0.0)}",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(16.dp)) // Space between Goal and Amount Saved

                            // Amount Saved Section
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.img_25), // Assuming a saved icon, or use checkmark
                                    contentDescription = "Amount Saved Icon",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Amount Saved", style = MaterialTheme.typography.titleSmall, color = Color.White.copy(alpha = 0.8f))
                            }
                            Text(
                                text = "$${String.format("%.2f", uiState.totalAmountSaved)}",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = DarkBlueProgress
                            )
                        }

                        // Right Box: Circular Progress Indicator
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(LightBlueCircle),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.matchParentSize()) {
                                val strokeWidth = 10.dp.toPx()
                                val center = Offset(size.width / 2, size.height / 2)
                                val radius = size.minDimension / 2 - strokeWidth / 2

                                drawCircle(
                                    color = Color.White.copy(alpha = 0.3f), // Base circle behind progress
                                    radius = radius,
                                    center = center,
                                    style = Stroke(width = strokeWidth)
                                )

                                drawArc(
                                    color = DarkBlueProgress, // Darker blue for progress
                                    startAngle = 270f,
                                    sweepAngle = 360 * (animatedProgressForCircle / 100f),
                                    useCenter = false,
                                    topLeft = Offset(center.x - radius, center.y - radius),
                                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                            Icon(
                                painter = painterResource(id = R.drawable.img_19), // Your plane icon
                                contentDescription = "Travel Goal",
                                modifier = Modifier.size(60.dp),
                                tint = DarkBlueProgress
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Linear Progress Bar (remains below)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            progress = animatedProgressForBar,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(16.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            color = DarkBlueProgress,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(0.8f),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (uiState.progressPercentage > 0f && uiState.progressPercentage < 1f) {
                                    "~1% Of Your Goal"
                                } else {
                                    "${uiState.progressPercentage.toInt()}% Of Your Goal"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "$${String.format("%.2f", uiState.travelGoal?.targetAmount ?: 0.0)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Transactions List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    val depositsToShow = uiState.travelDeposits
                        .filter { it.category == "TRAVEL" }
                        .sortedByDescending { it.date }

                    val groupedDeposits = depositsToShow.groupBy { Utils.getMonthName(it.date) }

                    if (uiState.travelGoal == null) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillParentMaxSize()
                                    .padding(vertical = 64.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "No Travel Goal set yet.",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Gray,
                                    fontFamily = FontFamily(Font(R.font.poppins_regular))
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Click the edit icon next to 'Goal' to begin!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    fontFamily = FontFamily(Font(R.font.poppins_regular))
                                )
                            }
                        }
                    } else if (depositsToShow.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillParentMaxSize()
                                    .padding(vertical = 64.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "No travel deposits recorded yet.",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Gray,
                                    fontFamily = FontFamily(Font(R.font.poppins_regular))
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Click 'Add Savings' below to make your first deposit!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    fontFamily = FontFamily(Font(R.font.poppins_regular))
                                )
                            }
                        }
                    }

                    groupedDeposits.forEach { (month, transactionsInMonth) ->
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = month,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                // New ellipsis icon instead of calendar
                                Icon(
                                    imageVector = Icons.Default.DateRange, // Ellipsis icon
                                    contentDescription = "More",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        items(transactionsInMonth) { saving ->
                            SavingsTransactionCard(saving = saving)
                        }
                    }
                }
            }
        }
    )
}

