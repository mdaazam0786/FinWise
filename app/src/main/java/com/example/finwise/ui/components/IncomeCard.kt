package com.example.finwise.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finwise.R
import com.example.finwise.data.model.expense.Expense
import com.example.finwise.data.model.income.Income
import com.example.finwise.util.Utils

@Composable
fun IncomeCard(incomeItem : Income) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFecf8f4))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFcce9fa)),
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = when(incomeItem.category){
                             "SALARY" -> R.drawable.img_25
                            "BUSINESS" -> R.drawable.img_26
                            "FREELANCE" -> R.drawable.img_27
                            "STOCK MARKET" -> R.drawable.img_28
                            "CRYPTO MARKET" -> R.drawable.img_29
                            "DIGITAL PRODUCTS" -> R.drawable.img_30
                            "OTHER" -> R.drawable.img_31
                            else -> R.drawable.img_25
                        }
                        Image(
                            painter = painterResource(id = icon), // Replace with your icon
                            contentDescription = "Transaction",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        val date = Utils.formatDateToHumanReadableForm(incomeItem.date)
                        Text(text = incomeItem.title, style = TextStyle(fontSize = 16.sp),fontFamily = FontFamily(
                            Font(R.font.poppins_regular)
                        )
                        )
                        Text(text = date, style = TextStyle(fontSize = 12.sp, color = Color(0xFF0068FF),fontFamily = FontFamily(
                            Font(R.font.poppins_regular)
                        )))
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "+$${incomeItem.amount}",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Green,fontFamily = FontFamily(
                        Font(R.font.poppins_regular)
                    ))
                )
            }
        }
    }
}