package com.example.finwise.ui.home.analysis

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finwise.data.chartData.ChartDataPoint
import com.example.finwise.data.chartData.TimeFrame
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
import kotlinx.coroutines.ExperimentalCoroutinesApi

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val repository: TransactionRepository // Repository now handles Income/Expense DAOs
) : ViewModel() {

    private val _selectedTimeFrame = MutableStateFlow(TimeFrame.DAILY)
    val selectedTimeFrame: StateFlow<TimeFrame> = _selectedTimeFrame.asStateFlow()

    // Represents the current reference date for fetching data
    // Using LocalDate is often easier for date logic than OffsetDateTime
    @RequiresApi(Build.VERSION_CODES.O)
    private val _referenceDate = MutableStateFlow(LocalDate.now())

    val totalBalance: StateFlow<Double> = repository.getTotalBalance()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Use flatMapLatest to react to timeframe changes

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    private val _periodicData = combine(
        _selectedTimeFrame,
        _referenceDate // React to date changes too, if you implement date picking
    ) { timeFrame, refDate -> Pair(timeFrame, refDate) } // Combine triggers
        .flatMapLatest { (timeFrame, refDate) ->
            // Get date range in millis using the repository's helper
            val (startMillis, endMillis) = repository.getTimeFrameRangeMillis(timeFrame, refDate)

            combine(
                repository.getPeriodicSummary(startMillis, endMillis),
                repository.getChartData(timeFrame, refDate) // Pass LocalDate here
            ) { summary, chartData ->
                PeriodicData(
                    income = summary.first,
                    expense = summary.second,
                    chartData = chartData
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PeriodicData()) // Initial empty data

    @RequiresApi(Build.VERSION_CODES.O)
    val periodIncome: StateFlow<Double> = _periodicData.map { it.income }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    @RequiresApi(Build.VERSION_CODES.O)
    val periodExpense: StateFlow<Double> = _periodicData.map { it.expense }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    @RequiresApi(Build.VERSION_CODES.O)
    val chartData: StateFlow<List<ChartDataPoint>> = _periodicData.map { it.chartData }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Expense progress state remains the same conceptually
    val expenseTarget = 20000.0 // Example Target
    @RequiresApi(Build.VERSION_CODES.O)
    val expensePercentage: StateFlow<Float> = periodExpense.map { expense ->
        if (expenseTarget > 0) (expense / expenseTarget).toFloat().coerceIn(0f, 1f) else 0f
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)


    fun selectTimeFrame(timeFrame: TimeFrame) {
        _selectedTimeFrame.value = timeFrame
    }



}

// Helper data class remains the same
data class PeriodicData(
    val income: Double = 0.0,
    val expense: Double = 0.0,
    val chartData: List<ChartDataPoint> = emptyList()
)
