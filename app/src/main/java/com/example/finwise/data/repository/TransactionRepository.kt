package com.example.finwise.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.finwise.data.chartData.ChartDataPoint
import com.example.finwise.data.chartData.TimeFrame
import com.example.finwise.data.dao.ExpenseDao
import com.example.finwise.data.dao.IncomeDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.* // Use java.time for easier date manipulation
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton


@Singleton // Repository is often a Singleton
class TransactionRepository @Inject constructor(
    private val incomeDao: IncomeDao, // Inject IncomeDao
    private val expenseDao: ExpenseDao // Inject ExpenseDao
) {

    // Combine total income and total expense for overall balance
    fun getTotalBalance(): Flow<Double> {
        return combine(
            incomeDao.getTotalIncomeSum().map { it ?: 0.0 },
            expenseDao.getTotalExpenseSum().map { it ?: 0.0 }
        ) { totalIncome, totalExpense ->
            totalIncome - totalExpense // Calculate balance
        }
    }

    // Get summary for a specific period
    fun getPeriodicSummary(startDate: Long, endDate: Long): Flow<Pair<Double, Double>> {
        return combine(
            incomeDao.getIncomeSumBetween(startDate, endDate).map { it ?: 0.0 },
            expenseDao.getExpenseSumBetween(startDate, endDate).map { it ?: 0.0 }
        ) { periodIncome, periodExpense ->
            Pair(periodIncome, periodExpense)
        }
    }

    // Get chart data, fetching both income and expenses for the period
    @RequiresApi(Build.VERSION_CODES.O)
    fun getChartData(timeFrame: TimeFrame, referenceDate: LocalDate = LocalDate.now()): Flow<List<ChartDataPoint>> {
        val (startDateLong, endDateLong) = getTimeFrameRangeMillis(timeFrame, referenceDate)

        // Combine flows of income list and expense list for the period
        return combine(
            incomeDao.getIncomeBetween(startDateLong, endDateLong),
            expenseDao.getExpensesBetween(startDateLong, endDateLong)
        ) { incomeList, expenseList ->
            // Process the combined lists based on the selected timeframe
            when (timeFrame) {
                TimeFrame.DAILY -> processForDailyChart(incomeList, expenseList, referenceDate)
                TimeFrame.WEEKLY -> processForWeeklyChart(incomeList, expenseList, referenceDate)
                TimeFrame.MONTHLY -> processForMonthlyChart(incomeList, expenseList, referenceDate)
                TimeFrame.YEARLY -> processForYearlyChart(incomeList, expenseList, referenceDate)
            }
        }
    }

    // --- Helper to get date range in Milliseconds ---
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeFrameRangeMillis(timeFrame: TimeFrame, referenceDate: LocalDate): Pair<Long, Long> {
        val zoneId = ZoneId.systemDefault() // Or choose a specific ZoneId
        val todayStart = referenceDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
        // End of day is start of *next* day minus 1 millisecond
        val todayEnd = referenceDate.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1

        val startMillis: Long
        val endMillis: Long

        when (timeFrame) {
            TimeFrame.DAILY -> {
                // Last 7 days ending on referenceDate
                startMillis = referenceDate.minusDays(6).atStartOfDay(zoneId).toInstant().toEpochMilli()
                endMillis = todayEnd
            }
            TimeFrame.WEEKLY -> {
                // Current week (e.g., Monday to Sunday) containing referenceDate
                val startOfWeek = referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val endOfWeek = startOfWeek.plusWeeks(1).minusDays(1) // End of Sunday
                startMillis = startOfWeek.atStartOfDay(zoneId).toInstant().toEpochMilli()
                endMillis = endOfWeek.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1
            }
            TimeFrame.MONTHLY -> {
                // Current month containing referenceDate
                val startOfMonth = referenceDate.with(TemporalAdjusters.firstDayOfMonth())
                val endOfMonth = referenceDate.with(TemporalAdjusters.lastDayOfMonth())
                startMillis = startOfMonth.atStartOfDay(zoneId).toInstant().toEpochMilli()
                endMillis = endOfMonth.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1
            }
            TimeFrame.YEARLY -> {
                // Current year containing referenceDate
                val startOfYear = referenceDate.with(TemporalAdjusters.firstDayOfYear())
                val endOfYear = referenceDate.with(TemporalAdjusters.lastDayOfYear())
                startMillis = startOfYear.atStartOfDay(zoneId).toInstant().toEpochMilli()
                endMillis = endOfYear.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1
            }
        }
        return Pair(startMillis, endMillis)
    }

    // --- Helper functions to process Income/Expense lists for chart ---

    @RequiresApi(Build.VERSION_CODES.O)
    private fun instantToLocalDate(millis: Long): LocalDate {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun processForDailyChart(
        incomeList: List<com.example.finwise.data.model.income.Income>, // Use fully qualified name if needed
        expenseList: List<com.example.finwise.data.model.expense.Expense>,
        referenceDate: LocalDate
    ): List<ChartDataPoint> {
        val dailyMap = mutableMapOf<DayOfWeek, Pair<Double, Double>>() // DayOfWeek -> (Income, Expense)
        val daysToShow = (0..6).map { referenceDate.minusDays(it.toLong()).dayOfWeek } .reversed() // Last 7 days ending today

        // Initialize map for all relevant days
        daysToShow.forEach { dailyMap[it] = Pair(0.0, 0.0) }

        // Group income
        incomeList.forEach { income ->
            val day = instantToLocalDate(income.date).dayOfWeek
            if (dailyMap.containsKey(day)) {
                dailyMap[day] = dailyMap[day]!!.copy(first = dailyMap[day]!!.first + income.amount)
            }
        }

        // Group expense
        expenseList.forEach { expense ->
            val day = instantToLocalDate(expense.date).dayOfWeek
            if (dailyMap.containsKey(day)) {
                dailyMap[day] = dailyMap[day]!!.copy(second = dailyMap[day]!!.second + expense.amount)
            }
        }

        // Map to ChartDataPoint using short day names (Mon, Tue, etc.)
        return daysToShow.map { dayOfWeek ->
            val sums = dailyMap[dayOfWeek] ?: Pair(0.0, 0.0)
            ChartDataPoint(
                label = dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()),
                income = sums.first,
                expense = sums.second
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun processForWeeklyChart(
        incomeList: List<com.example.finwise.data.model.income.Income>,
        expenseList: List<com.example.finwise.data.model.expense.Expense>,
        referenceDate: LocalDate
    ): List<ChartDataPoint> {
        // Group by week of month (e.g., W1, W2, W3, W4/W5) for the current month
        val weeklyMap = mutableMapOf<Int, Pair<Double, Double>>() // WeekOfMonth -> (Income, Expense)
        val weekFields = WeekFields.of(Locale.getDefault()) // Or specific locale
        val weeksInMonth = (1..referenceDate.with(TemporalAdjusters.lastDayOfMonth()).get(weekFields.weekOfMonth())).toList()

        weeksInMonth.forEach { weeklyMap[it] = Pair(0.0, 0.0) }

        incomeList.forEach { income ->
            val weekOfMonth = instantToLocalDate(income.date).get(weekFields.weekOfMonth())
            if (weeklyMap.containsKey(weekOfMonth)) {
                weeklyMap[weekOfMonth] = weeklyMap[weekOfMonth]!!.copy(first = weeklyMap[weekOfMonth]!!.first + income.amount)
            }
        }
        expenseList.forEach { expense ->
            val weekOfMonth = instantToLocalDate(expense.date).get(weekFields.weekOfMonth())
            if (weeklyMap.containsKey(weekOfMonth)) {
                weeklyMap[weekOfMonth] = weeklyMap[weekOfMonth]!!.copy(second = weeklyMap[weekOfMonth]!!.second + expense.amount)
            }
        }

        return weeksInMonth.map { weekNum ->
            val sums = weeklyMap[weekNum] ?: Pair(0.0, 0.0)
            ChartDataPoint(label = "W$weekNum", income = sums.first, expense = sums.second)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun processForMonthlyChart(
        incomeList: List<com.example.finwise.data.model.income.Income>,
        expenseList: List<com.example.finwise.data.model.expense.Expense>,
        referenceDate: LocalDate
    ): List<ChartDataPoint> {
        // Group by month for the current year
        val monthlyMap = mutableMapOf<Month, Pair<Double, Double>>() // Month -> (Income, Expense)
        val monthsToShow = Month.entries.toTypedArray() // All 12 months

        monthsToShow.forEach { monthlyMap[it] = Pair(0.0, 0.0) }

        incomeList.forEach { income ->
            val month = instantToLocalDate(income.date).month
            monthlyMap[month] = monthlyMap[month]!!.copy(first = monthlyMap[month]!!.first + income.amount)
        }
        expenseList.forEach { expense ->
            val month = instantToLocalDate(expense.date).month
            monthlyMap[month] = monthlyMap[month]!!.copy(second = monthlyMap[month]!!.second + expense.amount)
        }

        return monthsToShow.map { month ->
            val sums = monthlyMap[month] ?: Pair(0.0, 0.0)
            ChartDataPoint(
                label = month.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()),
                income = sums.first,
                expense = sums.second
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun processForYearlyChart(
        incomeList: List<com.example.finwise.data.model.income.Income>,
        expenseList: List<com.example.finwise.data.model.expense.Expense>,
        referenceDate: LocalDate // Use referenceDate to determine range if needed (e.g., last 5 years)
    ): List<ChartDataPoint> {
        // Group by Year (adjust logic if you want a specific range like last 5 years)
        val yearlyMap = mutableMapOf<Int, Pair<Double, Double>>() // Year -> (Income, Expense)

        // Determine years to show (e.g., based on data or a fixed range around referenceDate)
        val allYears = (incomeList.map { instantToLocalDate(it.date).year } +
                expenseList.map { instantToLocalDate(it.date).year }).toSortedSet()

        if (allYears.isEmpty()) return emptyList() // No data

        // Initialize map for all relevant years
        allYears.forEach { yearlyMap[it] = Pair(0.0, 0.0) }

        incomeList.forEach { income ->
            val year = instantToLocalDate(income.date).year
            if (yearlyMap.containsKey(year)) {
                yearlyMap[year] = yearlyMap[year]!!.copy(first = yearlyMap[year]!!.first + income.amount)
            }
        }
        expenseList.forEach { expense ->
            val year = instantToLocalDate(expense.date).year
            if (yearlyMap.containsKey(year)) {
                yearlyMap[year] = yearlyMap[year]!!.copy(second = yearlyMap[year]!!.second + expense.amount)
            }
        }

        return allYears.map { year ->
            val sums = yearlyMap[year] ?: Pair(0.0, 0.0)
            ChartDataPoint(label = year.toString(), income = sums.first, expense = sums.second)
        }
    }

}