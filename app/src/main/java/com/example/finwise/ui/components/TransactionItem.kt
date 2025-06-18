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
import com.example.finwise.data.model.transaction.Transaction
import com.example.finwise.data.model.transaction.TransactionType
import com.example.finwise.util.Utils


@Composable
fun TransactionItem(
    transaction : Transaction
) {

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
                        val icon = when(transaction.type){
                            TransactionType.EXPENSE -> when(transaction.category){
                                "FOOD" -> R.drawable.img_9
                                "GROCERIES" -> R.drawable.img_7
                                "ENTERTAINMENT" -> R.drawable.img_13
                                "RENT" -> R.drawable.img_6
                                "GIFTS" -> R.drawable.img_12
                                "SAVINGS" -> R.drawable.img_16
                                "TRANSPORT" -> R.drawable.img_10
                                "MEDICINE" -> R.drawable.img_11
                                else -> R.drawable.img_9
                            }
                            TransactionType.INCOME -> when(transaction.category){
                                "SALARY" -> R.drawable.img_25
                                "BUSINESS" -> R.drawable.img_26
                                "FREELANCE" -> R.drawable.img_27
                                "CRYPTO MARKET" -> R.drawable.img_28
                                "STOCK MARKET" -> R.drawable.img_29
                                "DIGITAL PRODUCTS" -> R.drawable.img_30
                                "OTHER" -> R.drawable.img_31
                                else -> R.drawable.img_1

                            }

                            TransactionType.SAVINGS -> TODO()
                            TransactionType.ASSETS -> TODO()
                            TransactionType.BALANCE -> TODO()
                        }
                        Image(
                            painter = painterResource(id = icon), // Replace with your icon
                            contentDescription = "Transaction",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        val date = Utils.formatDateToHumanReadableForm(transaction.date)
                        Text(text = transaction.title, style = TextStyle(fontSize = 16.sp, fontFamily = FontFamily(
                            Font(R.font.poppins_regular)
                        )))
                        Text(text = date, style = TextStyle(fontSize = 12.sp, color = Color(0xFF0068FF),fontFamily = FontFamily(
                            Font(R.font.poppins_regular)
                        )))
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                val amountColour = if(transaction.type == TransactionType.EXPENSE) Color(0xFF0068FF) else Color.Green
                val amountText = if (transaction.type == TransactionType.EXPENSE) "-$${transaction.amount}" else "+$${transaction.amount}"
                Text(

                    text = amountText,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, color = amountColour,fontFamily = FontFamily(
                        Font(R.font.poppins_regular)
                    ))


                )
            }
        }
    }
}