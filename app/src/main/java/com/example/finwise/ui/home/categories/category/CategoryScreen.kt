package com.example.finwise.ui.home.categories.category

import BottomNavigationBar
import com.example.finwise.ui.components.CentreTopBar
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
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
import com.example.finwise.ui.home.HomeViewModel
import com.example.finwise.util.Routes

@Composable
fun CategoriesScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController,

    ) {
    val expenses by homeViewModel.expenses.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    val incomes by homeViewModel.incomes.collectAsStateWithLifecycle(initialValue = emptyList())
    val balance = homeViewModel.getTotalBalance(expenses, incomes)
    val totalExpense = homeViewModel.totalExpense(expenses)

    Scaffold(
        topBar = {
            CentreTopBar(title = "Category", navController)
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
                // Top Section with balance
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
                        Column {
                            Text(
                                text = "Total Expense",
                                style = TextStyle(
                                    color = Color.White, fontSize = 14.sp, fontFamily = FontFamily(
                                        Font(R.font.poppins_regular)
                                    )
                                )
                            )
                            Text(
                                text = "-$totalExpense",
                                style = TextStyle(
                                    color = Color(0xff0068FF),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold, fontFamily = FontFamily(
                                        Font(R.font.poppins_regular)
                                    )
                                )
                            )
                        }
                    }
                }

                // White Box Section + Categories Grid
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 40.dp))
                        .background(Color(0xFFf0faf5))
                ) {
                    // List of Categories - Modify according to your categories

                    val categories = listOf(
                        Category("Food", R.drawable.img_9, Routes.FoodCategoryScreen),
                        Category("Transport", R.drawable.img_10, Routes.TransportCategoryScreen),
                        Category("Medicine", R.drawable.img_11, Routes.MedicineCategoryScreen),
                        Category("Groceries", R.drawable.img_7, Routes.GroceriesCategoryScreen),
                        Category("Rent", R.drawable.img_6, Routes.RentCategoryScreen),
                        Category("Gifts", R.drawable.img_12, Routes.GiftsCategoryScreen),
                        Category("Savings", R.drawable.img_8, Routes.SavingsCategoryScreen),
                        Category(
                            "Entertainment",
                            R.drawable.img_13,
                            Routes.EntertainmentCategoryScreen
                        ),
                    )

                    // Lazy Grid with Categories
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(categories) { category ->
                            CategoryItem(category = category, navController)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun CategoryItem(
    category: Category,
    navController: NavHostController
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF6DB6FE)) // Color is close to yours
            .size(100.dp)
            .clickable {
                navController.navigate(category.route)
            }
    ) {
        Icon(
            painter = painterResource(id = category.imageId),
            contentDescription = category.name,
            modifier = Modifier.size(40.dp),
            tint = Color.White
        )
    }
}

data class Category(
    val name: String,
    val imageId: Int,
    val route: String,
)

