package com.example.finwise.ui.home.analysis

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finwise.data.chartData.ChartDataPoint
import com.example.finwise.data.chartData.DailySummary
import com.example.finwise.data.chartData.TimeFrame
import com.example.finwise.data.dao.ExpenseDao
import com.example.finwise.data.dao.IncomeDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.Instant // Import Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import java.util.concurrent.TimeUnit // For dummy data date calculation
import javax.inject.Inject
import kotlin.random.Random // For dummy data

// Import your specific Income/Expense models if needed for dummy data
import com.example.finwise.data.model.income.Income
import com.example.finwise.data.model.expense.Expense
import com.example.finwise.data.repository.TransactionRepository
import com.example.finwise.util.Utils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val repository : TransactionRepository
) : ViewModel() {

    private val _selectedTimeFrame = MutableStateFlow(TimeFrame.DAILY)
    val selectedTimeFrame: StateFlow<TimeFrame> = _selectedTimeFrame.asStateFlow()

    // This will hold the data for the currently selected timeframe
    private val _graphData = MutableStateFlow(CombinedGraphData(LineGraphData(emptyList(), emptyList()), LineGraphData(emptyList(), emptyList())))
    val graphData: StateFlow<CombinedGraphData> = _graphData.asStateFlow()

    init {
        viewModelScope.launch {
            // flatMapLatest switches to a new Flow producer whenever _selectedTimeFrame changes
            _selectedTimeFrame.flatMapLatest { timeFrame ->
                when (timeFrame) {
                    TimeFrame.DAILY -> getDailyGraphData()
                    TimeFrame.WEEKLY -> getWeeklyGraphData()
                    TimeFrame.MONTHLY -> getMonthlyGraphData()
                    TimeFrame.YEARLY -> getYearlyGraphData()
                }
            }.collect { combinedData ->
                _graphData.value = combinedData
            }
        }
    }

    fun setTimeFrame(timeFrame: TimeFrame) {
        _selectedTimeFrame.value = timeFrame
    }

    // --- Data Fetching and Processing Functions ---

    private fun getDailyGraphData(): Flow<CombinedGraphData> {
        return combine(
            repository.getDailyExpenseSummaries(),
            repository.getDailyIncomeSummaries()
        ) { expenses, income ->
            val calendar = Calendar.getInstance()
            val today = System.currentTimeMillis()
            calendar.timeInMillis = today
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            // Get start of current week (Sunday)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            val startOfWeek = calendar.timeInMillis

            // Get end of current week (Saturday)
            calendar.add(Calendar.DATE, 6)
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            val endOfWeek = calendar.timeInMillis

            val dayLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            val expenseValues = MutableList(7) { 0.0f }
            val incomeValues = MutableList(7) { 0.0f }

            // Populate expense values for the current week
            expenses.filter { it.date in startOfWeek..endOfWeek }.forEach { summary ->
                calendar.timeInMillis = summary.date
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1=Sunday, 7=Saturday
                expenseValues[dayOfWeek - 1] += summary.totalAmount.toFloat()
            }

            // Populate income values for the current week
            income.filter { it.date in startOfWeek..endOfWeek }.forEach { summary ->
                calendar.timeInMillis = summary.date
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                incomeValues[dayOfWeek - 1] += summary.totalAmount.toFloat()
            }

            CombinedGraphData(
                expenseData = LineGraphData(dayLabels, expenseValues),
                incomeData = LineGraphData(dayLabels, incomeValues)
            )
        }
    }

    private fun getWeeklyGraphData(): Flow<CombinedGraphData> {
        return combine(
            repository.getWeeklyExpenseSummaries(),
            repository.getWeeklyIncomeSummaries()
        ) { expenses, income ->
            // Logic to filter for W1, W2, W3, W4 for the current month
            val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
            val calendar = Calendar.getInstance()
            calendar.time = Date() // Current date

            // Find the start of the current month
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val startOfMonth = calendar.timeInMillis

            // Find the end of the current month
            calendar.add(Calendar.MONTH, 1)
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            val endOfMonth = calendar.timeInMillis

            val weekLabels = mutableListOf<String>()
            val expenseValues = mutableListOf<Float>()
            val incomeValues = mutableListOf<Float>()

            val weeklyDataMapExpense = expenses
                .filter { it.week.startsWith(SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())) } // Filter for current year
                .associateBy { it.week }
            val weeklyDataMapIncome = income
                .filter { it.week.startsWith(SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())) } // Filter for current year
                .associateBy { it.week }

            // Generate labels for the last 4 weeks based on current date
            for (i in 3 downTo 0) { // Last 4 weeks including current
                calendar.time = Date()
                calendar.add(Calendar.WEEK_OF_YEAR, -i)
                val weekOfYear = SimpleDateFormat("yyyy-W", Locale.getDefault()).format(calendar.time)
                weekLabels.add("W${calendar.get(Calendar.WEEK_OF_YEAR)}") // Format as W1, W2 etc.

                expenseValues.add(weeklyDataMapExpense[weekOfYear]?.totalAmount?.toFloat() ?: 0f)
                incomeValues.add(weeklyDataMapIncome[weekOfYear]?.totalAmount?.toFloat() ?: 0f)
            }


            CombinedGraphData(
                expenseData = LineGraphData(weekLabels, expenseValues),
                incomeData = LineGraphData(weekLabels, incomeValues)
            )
        }
    }


    private fun getMonthlyGraphData(): Flow<CombinedGraphData> {
        return combine(
            repository.getMonthlyExpenseSummaries(),
            repository.getMonthlyIncomeSummaries()
        ) { expenses, income ->
            val monthLabels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            val currentYearPrefix = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())

            val expenseValues = MutableList(12) { 0.0f }
            val incomeValues = MutableList(12) { 0.0f }

            expenses.filter { it.month.startsWith(currentYearPrefix) }.forEach { summary ->
                val monthIndex = summary.month.substringAfter("-").toIntOrNull()?.minus(1)
                if (monthIndex != null && monthIndex in 0..11) {
                    expenseValues[monthIndex] += summary.totalAmount.toFloat()
                }
            }

            income.filter { it.month.startsWith(currentYearPrefix) }.forEach { summary ->
                val monthIndex = summary.month.substringAfter("-").toIntOrNull()?.minus(1)
                if (monthIndex != null && monthIndex in 0..11) {
                    incomeValues[monthIndex] += summary.totalAmount.toFloat()
                }
            }

            CombinedGraphData(
                expenseData = LineGraphData(monthLabels, expenseValues),
                incomeData = LineGraphData(monthLabels, incomeValues)
            )
        }
    }


    private fun getYearlyGraphData(): Flow<CombinedGraphData> {
        return combine(
            repository.getYearlyExpenseSummaries(),
            repository.getYearlyIncomeSummaries()
        ) { expenses, income ->
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val yearLabels = (currentYear - 2..currentYear + 2).map { it.toString() }

            val expenseValues = MutableList(5) { 0.0f }
            val incomeValues = MutableList(5) { 0.0f }

            val yearlyExpenseMap = expenses.associateBy { it.year }
            val yearlyIncomeMap = income.associateBy { it.year }

            yearLabels.forEachIndexed { index, year ->
                expenseValues[index] = yearlyExpenseMap[year]?.totalAmount?.toFloat() ?: 0f
                incomeValues[index] = yearlyIncomeMap[year]?.totalAmount?.toFloat() ?: 0f
            }

            CombinedGraphData(
                expenseData = LineGraphData(yearLabels, expenseValues),
                incomeData = LineGraphData(yearLabels, incomeValues)
            )
        }
    }

}

data class LineGraphData(
    val xValues: List<String>, // Labels for X-axis (e.g., "Mon", "Jan", "2025")
    val yValues: List<Float> // Y-axis values (e.g., amounts)
)

// Data class to hold combined expense and income graph data
data class CombinedGraphData(
    val expenseData: LineGraphData,
    val incomeData: LineGraphData
)
