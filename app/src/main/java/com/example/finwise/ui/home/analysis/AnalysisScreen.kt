package com.example.finwise.ui.home

import BottomNavigationBar
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.finwise.data.chartData.TimeFrame
import com.example.finwise.ui.components.CentreTopBar
import com.example.finwise.ui.home.analysis.AnalysisViewModel
import com.example.finwise.ui.home.analysis.BarGraph
import com.example.finwise.ui.home.analysis.PieChart

@Composable
fun AnalysisScreen(
    navController : NavHostController,
    viewModel: AnalysisViewModel = hiltViewModel()
) {

    val selectedTimeFrame = viewModel.selectedTimeFrame.collectAsState()
    val combinedGraphData = viewModel.graphData.collectAsState()
    val expensePieChartData = viewModel.expensePieChartData.collectAsState()

    Scaffold(
        topBar = {
            CentreTopBar(
                title = "Analysis",
                navController = navController
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(color = Color(0xFFf0faf5)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(1) {
                // Timeframe selection buttons
                Spacer(modifier = Modifier.height(16.dp))
                TimeFrameSelector(
                    selectedTimeFrame = selectedTimeFrame.value,
                    onTimeFrameSelected = { viewModel.setTimeFrame(it) }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Line Graph
                if (combinedGraphData.value.expenseData.yValues.isNotEmpty() || combinedGraphData.value.incomeData.yValues.isNotEmpty()) {
                    BarGraph(combinedGraphData = combinedGraphData.value)
                } else {
                    Text(
                        text = "No data available for the selected period.",
                        modifier = Modifier.padding(32.dp),
                        color = Color.Gray
                    )
                }

                // Optional: Legend for the graph colors
                Spacer(modifier = Modifier.height(16.dp))
                GraphLegend(expenseColor = Color.Blue, incomeColor = Color.Green)



                PieChart(pieChartData = expensePieChartData.value)
            }
        }
    }
}

@Composable
fun TimeFrameSelector(
    selectedTimeFrame: TimeFrame,
    onTimeFrameSelected: (TimeFrame) -> Unit
) {
    val timeFrames = remember { TimeFrame.entries.toTypedArray() } // Use entries for enum values

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .selectableGroup(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        timeFrames.forEach { timeFrame ->
            val isSelected = selectedTimeFrame == timeFrame
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .selectable(
                        selected = isSelected,
                        onClick = { onTimeFrameSelected(timeFrame) },
                        role = Role.RadioButton
                    ),
                shape = MaterialTheme.shapes.small,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                shadowElevation = 2.dp
            ) {
                Text(
                    text = timeFrame.name.lowercase().replaceFirstChar { it.uppercase() }, // "Daily", "Weekly"
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                    textAlign = TextAlign.Center, // FIX: Use TextAlign.Center directly
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun GraphLegend(expenseColor: Color, incomeColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(color = expenseColor, text = "Expenses")
        LegendItem(color = incomeColor, text = "Income")
    }
}
@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(
            modifier = Modifier
                .size(16.dp)
                .background(color, MaterialTheme.shapes.small)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall)
    }
}





