package com.example.finwise.ui.home.categories.food

import BottomNavigationBar
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.finwise.R
import com.example.finwise.data.model.expense.Expense
import com.example.finwise.ui.components.CentreTopBar
import com.example.finwise.ui.components.MultiFloatingActionButton
import com.example.finwise.ui.components.TransactionItem
import com.example.finwise.ui.home.HomeViewModel
import com.example.finwise.util.Routes
import com.example.finwise.util.Utils
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Composable
fun FoodExpensesScreen(
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
            CentreTopBar(title = "Food", navController = navController)

        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        floatingActionButton = {
            MultiFloatingActionButton(navController)
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
                            .padding(top = 16.dp)
                    ) {
                        val groupedTransactions = expenses.groupBy { Utils.getMonthName(it.date) }

                        groupedTransactions.forEach { (month, transactionsInMonth) ->
                            val foodExpensesInMonth = transactionsInMonth.filter { it.category == "FOOD" }
                            if(foodExpensesInMonth.isNotEmpty()) {
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
                                if(it.category == "FOOD"){
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

@Composable
fun ExpenseCard(expenseItem: Expense) {
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
                        val icon = when(expenseItem.category){
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
                        Image(
                            painter = painterResource(id = icon), // Replace with your icon
                            contentDescription = "Transaction",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        val date = Utils.formatDateToHumanReadableForm(expenseItem.date)
                        Text(text = expenseItem.title, style = TextStyle(fontSize = 16.sp),fontFamily = FontFamily(
                            Font(R.font.poppins_regular)
                        ))
                        Text(text = date, style = TextStyle(fontSize = 12.sp, color = Color(0xFF0068FF),fontFamily = FontFamily(
                            Font(R.font.poppins_regular)
                        )))
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "-$${expenseItem.amount}",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF0068FF),fontFamily = FontFamily(
                        Font(R.font.poppins_regular)
                    ))
                )
            }
    }
        }
}