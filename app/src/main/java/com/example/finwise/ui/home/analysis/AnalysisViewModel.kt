package com.example.finwise.ui.home.analysis

// Import your specific Income/Expense models if needed for dummy data
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finwise.data.chartData.TimeFrame
import com.example.finwise.data.model.transaction.CategoryTotal
import com.example.finwise.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val repository : TransactionRepository
) : ViewModel() {

    private val _expensePieChartData = MutableStateFlow(PieChartData(emptyList(), 0f))
    val expensePieChartData: StateFlow<PieChartData> = _expensePieChartData.asStateFlow()


    private val _selectedTimeFrame = MutableStateFlow(TimeFrame.DAILY)
    val selectedTimeFrame: StateFlow<TimeFrame> = _selectedTimeFrame.asStateFlow()

    // This will hold the data for the currently selected timeframe
    private val _graphData = MutableStateFlow(CombinedGraphData(LineGraphData(emptyList(), emptyList()), LineGraphData(emptyList(), emptyList())))
    val graphData: StateFlow<CombinedGraphData> = _graphData.asStateFlow()

    private val categoryColors = mapOf(
        "FOOD" to Color(0xFFF44336),     // Red
        "TRANSPORT" to Color(0xFFE91E63), // Pink
        "GROCERIES" to Color(0xFF9C27B0), // Purple
        "RENT" to Color(0xFF673AB7),      // Deep Purple
        "GIFTS" to Color(0xFF3F51B5),  // Indigo
        "ENTERTAINMENT" to Color(0xFF2196F3), // Blue
        "MEDICINE" to Color(0xFF03A9F4),    // Light Blue
    )

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
        viewModelScope.launch {
            repository.getExpenseTotalsByCategory()
                .map { categoryTotals ->
                    processPieChartData(categoryTotals)
                }
                .collect { pieChartData ->
                    _expensePieChartData.value = pieChartData
                }
        }
    }

    private fun processPieChartData(categoryTotals: List<CategoryTotal>): PieChartData {
        val totalExpense = categoryTotals.sumOf { it.totalAmount }.toFloat()
        if (totalExpense == 0f) {
            return PieChartData(emptyList(), 0f)
        }

        val slices = categoryTotals.map { categoryTotal ->
            val percentage = (categoryTotal.totalAmount.toFloat() / totalExpense) * 100

            PieChartSlice(
                category = categoryTotal.categoryName,
                value = categoryTotal.totalAmount.toFloat(),
                percentage = percentage,
                color = categoryColors[categoryTotal.categoryName] ?: categoryColors["Other"] ?: Color.Gray // Assign color, default to gray
            )
        }.sortedByDescending { it.value } // Sort slices by value for better visualization

        return PieChartData(slices, totalExpense)
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
data class PieChartSlice(
    val category: String,
    val value: Float,
    val percentage: Float,
    val color: Color
)

// NEW: Data class for the entire Pie Chart data
data class PieChartData(
    val slices: List<PieChartSlice>,
    val totalExpense: Float
)
