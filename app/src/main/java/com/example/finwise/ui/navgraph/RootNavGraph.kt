package com.example.finwise.ui.navgraph

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.finwise.ui.home.HomeViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun RootNavGraph(
    navController : NavHostController,
    scope : CoroutineScope,
    onBoardingComplete : Preferences.Key<Boolean>,
    datastore : DataStore<Preferences>,
    currentUser : FirebaseUser?
) {
    val viewModel = hiltViewModel<HomeViewModel>()
    
    NavHost(navController = navController, 
        startDestination = 
        if (currentUser != null) {
            Graph.Home
        }else{
            Graph.Auth
        },
        route = Graph.Root
    ) {
        authNavGraph(
            scope = scope,
            datastore = datastore,
            onBoardingComplete = onBoardingComplete,
            navController = navController
        )
        homeNavGraph(
            navController = navController,
            viewModel = viewModel
        )
    }
    
}


object Graph {
    const val Root = "root_graph"
    const val Auth = "auth_graph"
    const val Home = "home_graph"

}