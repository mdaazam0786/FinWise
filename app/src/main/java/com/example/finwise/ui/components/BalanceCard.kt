package com.example.finwise.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finwise.data.model.transaction.Transaction
import com.example.finwise.data.model.transaction.TransactionType

@Composable
fun BalanceCard(
    modifier: Modifier = Modifier,
    balance : String,
    title : String,
    transaction: TransactionType,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column {
            val color = if(transaction == TransactionType.EXPENSE) Color(0xFF0068FF) else Color(0xFF004f4b)
            Text(
                text = title,
                style = TextStyle(
                    color = color, // Dark teal color for the label
                    fontSize = 16.sp
                )
            )
            Text(
                text = balance,
                style = TextStyle(
                    color = color, // Dark teal color for the amount
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }


}