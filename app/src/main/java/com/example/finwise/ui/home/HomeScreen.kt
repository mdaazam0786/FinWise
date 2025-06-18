package com.example.finwise.ui.home


import BottomNavigationBar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.finwise.R // Replace with your actual package name
import com.example.finwise.ui.components.HomeTopBar
import com.example.finwise.ui.components.TransactionItem
import com.example.finwise.util.Utils

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val expenses by viewModel.expenses.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    val incomes by viewModel.incomes.collectAsStateWithLifecycle(initialValue = emptyList())
    val balance = viewModel.getTotalBalance(expenses,incomes)
    val totalExpense = viewModel.totalExpense(expenses)

    val transactions = viewModel.getAllTransactions(expenses,incomes)
    
    Scaffold(
        topBar = {
            HomeTopBar(title = "Hi, Welcome Back")
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFf0faf5))
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Section

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color(0xFF20c997)),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 62.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total Balance",
                                style = TextStyle(
                                    color = Color.White, fontSize = 14.sp, fontFamily = FontFamily(
                                        Font(R.font.poppins_regular)
                                    )
                                )
                            )
                            Text(
                                text = balance,
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold, fontFamily = FontFamily(
                                        Font(R.font.poppins_regular)
                                    )
                                )
                            )
                        }
                        Column{
                            Text(
                                text = "Total Expense",
                                style = TextStyle(
                                    color = Color.White, fontSize = 14.sp, fontFamily = FontFamily(
                                        Font(R.font.poppins_regular)
                                    )
                                )
                            )
                            Text(
                                text = "-${totalExpense}",
                                style = TextStyle(
                                    color = Color(0xff0068FF),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,fontFamily = FontFamily(
                                        Font(R.font.poppins_regular))
                                )
                            )
                        }
                    }
                }


                // White Box Section
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                        .background(Color(0xFFf0faf5))
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxSize()
                    ) {
                        val groupedTransactions = transactions.groupBy { Utils.getMonthName(it.date) }

                        groupedTransactions.forEach { (month, transactionsInMonth) ->
                            item {
                                Text(
                                    text = month,
                                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                            items(transactionsInMonth) {
                                TransactionItem(transaction = it)
                            }
                        }

                    }
                }
            }

        }
    )
}


