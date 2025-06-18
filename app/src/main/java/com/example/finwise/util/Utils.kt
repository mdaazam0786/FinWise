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



}
