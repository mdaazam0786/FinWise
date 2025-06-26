package com.example.finwise.ui.home.categories.savings

import BottomNavigationBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.finwise.ui.components.CentreTopBar
import com.example.finwise.ui.home.HomeViewModel
import com.example.finwise.util.Routes

data class SavingsItem(val name: String, val imageId: Int, val route : String)

@Composable
fun SavingsScreen(
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
            CentreTopBar(title = "Savings", navController = navController)
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
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
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 62.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Total Balance",
                                style = TextStyle(
                                    color = Color.White, fontSize = 14.sp
                                )
                            )
                            Text(
                                text = balance,
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Total Expense",
                                style = TextStyle(
                                    color = Color.White, fontSize = 14.sp
                                )
                            )
                            Text(
                                text = "-$totalExpense",
                                style = TextStyle(
                                    color = Color(0xff0068FF),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                // White Box Section with Savings Goals
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                        .background(Color(0xFFf0faf5))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val goals = listOf(
                            SavingsItem("Travel", R.drawable.img_19, Routes.TravelSavingsScreen),
                            SavingsItem("New House", R.drawable.img_20, Routes.HomeSavingsScreen),
                            SavingsItem("Car", R.drawable.img_21, Routes.CarSavingsScreen),
                            SavingsItem("Wedding", R.drawable.img_22, Routes.WeddingSavingsScreen)
                        )

                        // List of Goals Icons
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(16.dp),
                            modifier = Modifier
                                .weight(1f) // Take up the remaining space above the button
                                .fillMaxWidth()
                        ) {
                            items(goals) { goal ->
                                GoalItem(goal = goal, navController = navController)
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun GoalItem(goal: SavingsItem , navController : NavHostController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(8.dp)
            .clickable{
                navController.navigate(goal.route)
            }
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF6DB6FE)) // Light background color
            .size(100.dp) // Adjust size as needed
    ) {
        Icon(
            painter = painterResource(id = goal.imageId),
            contentDescription = goal.name,
            modifier = Modifier.size(40.dp), // Adjust icon size
            tint = Color.White
        )

    }
}