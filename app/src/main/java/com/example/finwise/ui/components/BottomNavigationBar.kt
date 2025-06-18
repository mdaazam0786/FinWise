import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationDefaults
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.finwise.R // Replace with your actual package name
import com.example.finwise.util.Routes

@Composable
fun BottomNavigationBar(
    navController: NavController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Analysis,
        BottomNavItem.Transaction,
        BottomNavItem.Categories,
        BottomNavItem.Profile
    )
    BottomNavigation(
        backgroundColor = Color(0xffDFF7E2),
        modifier = Modifier.clip(RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp)).height(70.dp)
    ) {
        bottomBarItems.forEachIndexed { index,item ->
            val isSelected = currentRoute == item.route
            BottomNavigationItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) { saveState = true }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp) // Size of the icon's background
                            .clip(CircleShape) // Make it a circle
                            .background(if (isSelected) Color(0xFF20c997) else Color.Transparent) // Apply teal if selected
                    ) {
                        val iconResourceId = item.icon

                        Icon(
                            modifier = Modifier.size(25.dp),
                            painter = painterResource(id = iconResourceId),
                            contentDescription = null,
                            tint = if (isSelected) Color(0xffDFF7E2) else Color.Black //Color for tint
                        )
                    }

                },
            )
        }
    }

}

sealed class BottomNavItem(val route: String, val icon: Int, val label: String) {
    object Home : BottomNavItem(Routes.HomeScreen, R.drawable.img_14, "Home")
    object Analysis : BottomNavItem(Routes.AnalysisScreen, R.drawable.img_15, "Search")
    object Transaction : BottomNavItem(Routes.TransactionScreen, R.drawable.img_16, "Transaction")
    object Categories : BottomNavItem(Routes.CategoryScreen, R.drawable.img_17, "Categories")
    object Profile : BottomNavItem(Routes.ProfileScreen, R.drawable.img_18, "Profile")
}