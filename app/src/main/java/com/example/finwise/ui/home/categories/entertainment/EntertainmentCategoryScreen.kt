package com.example.finwise.ui.home.categories.entertainment

import BottomNavigationBar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.finwise.R
import com.example.finwise.ui.components.CentreTopBar
import com.example.finwise.ui.home.HomeViewModel
import com.example.finwise.ui.home.categories.food.ExpenseCard
import com.example.finwise.util.Routes
import com.example.finwise.util.Utils

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EntertainmentCategoryScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val expenses by homeViewModel.expenses.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    val incomes by homeViewModel.incomes.collectAsStateWithLifecycle(initialValue = emptyList())
    val balance = homeViewModel.getTotalBalance(expenses, incomes)
    val totalExpense = homeViewModel.totalExpense(expenses)

    Scaffold(
        topBar = {
            CentreTopBar(title = "Entertainment", navController = navController)

        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        floatingActionButton = {

            IconButton(
                onClick = {
                    navController.navigate(Routes.AddExpenseScreen)

                },
                colors = IconButtonDefaults.iconButtonColors(Color(0xFF20c997)),
                modifier = Modifier.size(60.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }

        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFf0faf5))
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Section with balance and progress bar
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
                                    color = Color.White, fontSize = 14.sp,fontFamily = FontFamily(
                                        Font(R.font.poppins_regular)
                                    )
                                )
                            )
                            Text(
                                text = balance,
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,fontFamily = FontFamily(
                                        Font(R.font.poppins_regular)
                                    )
                                )
                            )
                        }
                        Column{
                            Text(
                                text = "Total Expense",
                                style = TextStyle(
                                    color = Color.White, fontSize = 14.sp,fontFamily = FontFamily(
                                        Font(R.font.poppins_regular)
                                    )
                                )
                            )
                            Text(
                                text = "-$totalExpense",
                                style = TextStyle(
                                    color = Color(0xff0068FF),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,fontFamily = FontFamily(
                                        Font(R.font.poppins_regular)
                                    )
                                )
                            )
                        }
                    }
                }

                // White Box Section + List of expenses
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 40.dp))
                        .background(Color(0xFFf0faf5))
                ) {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        val groupedTransactions = expenses.groupBy { Utils.getMonthName(it.date) }

                        groupedTransactions.forEach { (month, transactionsInMonth) ->
                            val entertainmentExpensesInMonth = transactionsInMonth.filter { it.category == "ENTERTAINMENT" }
                            if(entertainmentExpensesInMonth.isNotEmpty()) {
                                item {
                                    Text(
                                        text = month,
                                        style = TextStyle(
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily(
                                                Font(R.font.poppins_regular)
                                            )
                                        ),
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                            items(transactionsInMonth) {
                                if(it.category == "ENTERTAINMENT"){
                                    ExpenseCard(expenseItem = it)
                                }else{
                                    //Do Nothing
                                }
                            }
                        }

                    }
                }
            }
        }
    )

}