package com.example.finwise.util

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.finwise.data.model.expense.Expense
import java.io.ByteArrayOutputStream
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Utils {

    fun formatDateToHumanReadableForm(date: Long): String {
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormatter.format(date)
    }

    fun getMonthName(date : Long) : String{

        val instant = Instant.ofEpochMilli(date)
        val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()

        val formatter  = DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())

        return localDate.format(formatter)

    }
    fun formatCurrency(amount: Double, locale : Locale = Locale.US): String {
        val currencyFormatter = NumberFormat.getCurrencyInstance(locale)
        return currencyFormatter.format(amount)
    }


    @SuppressLint("SimpleDateFormat")
    fun getMilliFromDate(dateFormat: String): Long {
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        return try {
            val date = formatter.parse(dateFormat)!!
            date.time ?: 0L
        } catch (e: ParseException) {
            e.printStackTrace()
            0L
        }
    }

    fun getStartAndEndOfToday(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val start = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val end = calendar.timeInMillis

        return Pair(start, end)
    }

    fun getStartAndEndOfWeek(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val start = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val end = calendar.timeInMillis

        return Pair(start, end)
    }

    fun getStartAndEndOfMonth(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val start = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val end = calendar.timeInMillis

        return Pair(start, end)
    }

    fun getStartAndEndOfYear(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_YEAR, 1)
        val start = calendar.timeInMillis

        calendar.add(Calendar.YEAR, 1)
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val end = calendar.timeInMillis

        return Pair(start, end)
    }


}
