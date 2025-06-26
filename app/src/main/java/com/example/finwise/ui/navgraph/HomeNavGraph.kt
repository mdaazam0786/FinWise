package com.example.finwise.ui.navgraph

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.finwise.data.User
import com.example.finwise.ui.home.AnalysisScreen
import com.example.finwise.ui.home.HomeScreen
import com.example.finwise.ui.home.HomeViewModel
import com.example.finwise.ui.home.addSavings.AddSavingsScreen
import com.example.finwise.ui.home.addexpense.AddExpensesScreen
import com.example.finwise.ui.home.addincome.AddIncomesScreen
import com.example.finwise.ui.home.categories.category.CategoriesScreen
import com.example.finwise.ui.home.categories.entertainment.EntertainmentCategoryScreen
import com.example.finwise.ui.home.categories.food.FoodExpensesScreen
import com.example.finwise.ui.home.categories.gifts.GiftsCategoryScreen
import com.example.finwise.ui.home.categories.groceries.GroceriesCategoryScreen
import com.example.finwise.ui.home.categories.medicine.MedicineCategoryScreen
import com.example.finwise.ui.home.categories.rent.RentCategoryScreen
import com.example.finwise.ui.home.categories.savings.SavingsScreen
import com.example.finwise.ui.home.categories.savings.car.CarSavingsScreen
import com.example.finwise.ui.home.categories.savings.house.HouseSavingsScreen
import com.example.finwise.ui.home.categories.savings.travel.TravelSavingsScreen
import com.example.finwise.ui.home.categories.savings.wedding.WeddingSavingsScreen
import com.example.finwise.ui.home.categories.transport.TransportCategoryScreen
import com.example.finwise.ui.home.profile.ProfileScreen
import com.example.finwise.ui.home.profile.editprofile.EditProfileScreen
import com.example.finwise.ui.home.transaction.TransactionScreen
import com.example.finwise.util.Routes


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController,
    viewModel: HomeViewModel
){

    navigation(startDestination = Routes.HomeScreen, route = Graph.Home) {
        composable(route = Routes.HomeScreen) {
            HomeScreen(viewModel, navController)
        }
        composable(route = Routes.AnalysisScreen) {
            AnalysisScreen(navController = navController)
        }
        composable(route = Routes.ProfileScreen) {
            ProfileScreen(
                navController = navController,
            )
        }
        composable(route = Routes.EditProfileScreen) {
            EditProfileScreen(navController = navController)
        }
        composable(route = Routes.TransactionScreen) {
            TransactionScreen(navController)
        }
        composable(Routes.CategoryScreen) {
            CategoriesScreen(viewModel, navController)
        }
        composable(route = Routes.FoodCategoryScreen) {
            FoodExpensesScreen(navController = navController, homeViewModel = viewModel)
        }
        composable(route = Routes.TransportCategoryScreen) {
            TransportCategoryScreen(navController = navController)
        }
        composable(route = Routes.MedicineCategoryScreen) {
            MedicineCategoryScreen(navController = navController)
        }
        composable(route = Routes.GroceriesCategoryScreen) {
            GroceriesCategoryScreen(navController = navController)
        }
        composable(route = Routes.RentCategoryScreen) {
            RentCategoryScreen(navController = navController)
        }
        composable(route = Routes.GiftsCategoryScreen) {
            GiftsCategoryScreen(navController = navController)
        }
        composable(route = Routes.EntertainmentCategoryScreen) {
            EntertainmentCategoryScreen(navController = navController)
        }
        composable(route = Routes.SavingsCategoryScreen) {
            SavingsScreen(navController = navController)
        }
        composable(route = Routes.HomeSavingsScreen){
            HouseSavingsScreen(
                navController = navController
            )
        }
        composable(route = Routes.CarSavingsScreen){
            CarSavingsScreen(
                navController = navController
            )
        }
        composable(route = Routes.WeddingSavingsScreen){
            WeddingSavingsScreen(
                navController = navController
            )
        }
        composable(route = Routes.TravelSavingsScreen){
            TravelSavingsScreen(navController = navController)
        }

        composable(route = Routes.AddExpenseScreen) {
            AddExpensesScreen(navController = navController)
        }
        composable(route = Routes.AddIncomeScreen) {
            AddIncomesScreen(navController = navController)
        }
        composable(route = Routes.AddSavingsScreen){
            AddSavingsScreen(navController = navController)
        }
    }
}