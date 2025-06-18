package com.example.finwise.ui.home

import BottomNavigationBar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons // Import necessary icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel // If using viewModel() delegate
import androidx.navigation.NavHostController
import com.example.finwise.ui.home.analysis.AnalysisViewModel
import com.example.finwise.ui.home.analysis.TimeFrameSelector
import com.example.finwise.R
import com.example.finwise.ui.components.CentreTopBar
import com.example.finwise.ui.home.analysis.IncomeExpenseChartCard
import com.example.finwise.ui.home.analysis.IncomeExpenseSummaryItem

// Assume other necessary imports (Color, etc.)

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun AnalysisScreen(
    modifier: Modifier = Modifier, // Allow passing modifiers from parent
    viewModel: AnalysisViewModel = hiltViewModel(),
    navController : NavHostController// Pass your actual ViewModel instance here

) {
    // Collect state from the ViewModel
    // (Use this approach if passing the whole ViewModel)
    val selectedTimeFrame by viewModel.selectedTimeFrame.collectAsState()
    val chartData by viewModel.chartData.collectAsState()
    val periodIncome by viewModel.periodIncome.collectAsState()
    val periodExpense by viewModel.periodExpense.collectAsState()

    Scaffold(
        topBar = {
            CentreTopBar(title = "Analysis", navController = navController)
        },
        bottomBar = {
                    BottomNavigationBar(navController = navController)
        },
        content = {
        Column(
            modifier = modifier
                .fillMaxSize()
                // Add horizontal padding common to this section
                .padding(it)
            // Add bottom padding if this is the last element before a BottomBar
            // .padding(bottom = 80.dp) // Adjust as needed
        ) {
            // 1. Time Frame Selector
            Spacer(modifier = Modifier.height(24.dp))
            TimeFrameSelector(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                // .offset(y = (-30).dp), // Uncomment to pull it up slightly if needed
                selectedTimeFrame = selectedTimeFrame,
                onTimeFrameSelected = { timeFrame -> viewModel.selectTimeFrame(timeFrame) }, // Call VM function
                backgroundColor = colorResource(id = R.color.light_green),
                selectedColor = colorResource(id = R.color.main_green),
                unselectedColor = Color.DarkGray,
                selectedTextColor = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp)) // Space after selector

            // 2. Income/Expense Chart Card
            IncomeExpenseChartCard(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                chartData = chartData,
                timeFrame = selectedTimeFrame, // Pass for context if needed
                backgroundColor = colorResource(id = R.color.main_green).copy(alpha = 0.1f),
                incomeBarColor = colorResource(id = R.color.main_green),
                expenseBarColor = colorResource(id = R.color.ocean_blue),
                labelColor = colorResource(id = R.color.letters_icons),
                gridLineColor = colorResource(id = R.color.light_blue),
                // Add onSearchClick = {}, onCalendarClick = {} if needed
            )

            Spacer(modifier = Modifier.height(24.dp)) // Space after chart card

            // 3. Income/Expense Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly, // Space items evenly
                verticalAlignment = Alignment.Top // Align tops of items
            ) {
                IncomeExpenseSummaryItem(
                    label = "Income",
                    amount = periodIncome,
                    icon = Icons.Outlined.KeyboardArrowUp, // Suitable icon
                    iconTint = colorResource(id = R.color.main_green),
                    amountColor = colorResource(id = R.color.letters_icons) // Or incomeGreenBar
                )
                IncomeExpenseSummaryItem(
                    label = "Expense",
                    amount = periodExpense,
                    icon = Icons.Outlined.KeyboardArrowDown, // Suitable icon
                    iconTint = colorResource(id = R.color.ocean_blue),
                    amountColor = colorResource(id = R.color.ocean_blue)
                )
            }

            Spacer(modifier = Modifier.height(32.dp)) // Space before Targets


        } // End Main Column
    })


}