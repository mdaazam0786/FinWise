package com.example.finwise.ui.navgraph

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.finwise.ui.auth.login.LoginScreen
import com.example.finwise.ui.auth.login2.LoginScreen2
import com.example.finwise.ui.auth.onBoarding.OnboardingPager
import com.example.finwise.ui.auth.signup.SignUpScreen
import com.example.finwise.util.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


fun NavGraphBuilder.authNavGraph(
    navController : NavHostController,
    scope : CoroutineScope,
    onBoardingComplete : Preferences.Key<Boolean>,
    datastore : DataStore<Preferences>
){
    navigation(startDestination = Routes.onBoardingScreen, route = Graph.Auth) {
        composable(route = Routes.onBoardingScreen) {
            OnboardingPager(
                onFinish = {
                    scope.launch {
                        datastore.edit { preferences ->
                            preferences[onBoardingComplete] = false
                        }
                    }
                },
                navController = navController
            )
        }
        composable(route = Routes.LoginScreen) {
            LoginScreen(
                onNavigate = {
                    navController.navigate(it.route)
                }
            )
        }
        composable(route = Routes.LoginScreen2) {
            LoginScreen2(
                onNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(Routes.LoginScreen2) { inclusive = true }
                    }
                },
            )
        }
        composable(route = Routes.SignupScreen) {
            SignUpScreen(
                onNavigate = {
                    navController.navigate(it.route)
                }
            )
        }
    }

}