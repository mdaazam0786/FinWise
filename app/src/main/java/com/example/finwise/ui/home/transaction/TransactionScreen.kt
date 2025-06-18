package com.example.finwise.ui.home.transaction

import BottomNavigationBar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.finwise.R
import com.example.finwise.data.model.transaction.TransactionType
import com.example.finwise.ui.components.BalanceCard
import com.example.finwise.ui.components.CentreTopBar
import com.example.finwise.ui.components.IncomeCard
import com.example.finwise.ui.components.TransactionItem
import com.example.finwise.ui.home.HomeViewModel
import com.example.finwise.ui.home.categories.food.ExpenseCard
import com.example.finwise.util.Utils

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    transactionViewModel: TransactionViewModel = hiltViewModel()
) {
    val expenses by homeViewModel.expenses.collectAsStateWithLifecycle(initialValue = emptyList())
    val incomes by homeViewModel.incomes.collectAsStateWithLifecycle(initialValue = emptyList())
    val balance = homeViewModel.getTotalBalance(expenses,incomes)
    val totalExpense = homeViewModel.totalExpense(expenses)
    val totalIncome = homeViewModel.totalIncome(incomes)

    var transactionCategory by remember {
        mutableStateOf(TransactionType.BALANCE)
    }

    val transactions = homeViewModel.getAllTransactions(expenses,incomes)


    Scaffold(
        topBar = {
                 CentreTopBar(title = "Transaction", navController = navController )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFecf8f4))
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(Color(0xFF20c997)),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    BalanceCard(
                        balance = balance,
                        title = "Total Balance",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(16.dp)
                            .background(
                                color = Color(0xFFecf8f4),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                transactionCategory = TransactionType.BALANCE
                            }
                        ,

                        transaction = TransactionType.INCOME
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        BalanceCard(
                            balance = totalIncome,
                            title = "Total Income",
                            modifier = Modifier
                                .padding(16.dp)
                                .height(120.dp)
                                .weight(0.5f)
                                .background(
                                    color = Color(0xFFecf8f4),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {

                                    transactionCategory = TransactionType.INCOME
                                },
                            transaction = TransactionType.INCOME
                        )
                        BalanceCard(
                            balance = totalExpense,
                            title = "Total Expense",
                            modifier = Modifier
                                .padding(16.dp)
                                .height(120.dp)
                                .weight(0.5f)
                                .background(
                                    color = Color(0xFFecf8f4),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    transactionCategory = TransactionType.EXPENSE
                                },
                            transaction = TransactionType.EXPENSE
                        )
                    }
                }

                // Income and expenses summary
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
                        val sortedExpenses = transactionViewModel.getALlExpenses(expenses)
                        val sortedIncome = transactionViewModel.getAllIncomes(incomes)
                        val groupedIncomes = sortedIncome.groupBy { Utils.getMonthName(it.date) }
                        val groupedExpenses = sortedExpenses.groupBy { Utils.getMonthName(it.date) }

                        when(transactionCategory) {
                            TransactionType.BALANCE -> {
                                groupedTransactions.forEach { (month, transactionsInMonth) ->
                                    item {
                                        Text(
                                            text = month,
                                            style = TextStyle(
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                    }
                                    items(transactionsInMonth) {
                                        TransactionItem(transaction = it)
                                    }
                                }
                            }
                            TransactionType.INCOME -> {
                                groupedIncomes.forEach { (month, transactionsInMonth) ->
                                    item {
                                        Text(
                                            text = month,
                                            style = TextStyle(
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                    }
                                    items(transactionsInMonth) {
                                        IncomeCard(incomeItem = it)
                                    }
                                }
                            }
                            TransactionType.EXPENSE -> {
                                groupedExpenses.forEach { (month, transactionsInMonth) ->
                                    item {
                                        Text(
                                            text = month,
                                            style = TextStyle(
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                    }
                                    items(transactionsInMonth) {
                                        ExpenseCard(expenseItem = it)
                                    }
                                }
                            }
                            else -> {}//Todo()
                        }
                    }
                }
            }
        }
    )
}

