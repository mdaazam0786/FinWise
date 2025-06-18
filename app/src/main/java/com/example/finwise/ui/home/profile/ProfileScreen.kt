package com.example.finwise.ui.home.profile // Use your actual package

import BottomNavigationBar
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn // Use LazyColumn if list can grow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.* // Import necessary icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.finwise.R
import com.example.finwise.data.User
import com.example.finwise.ui.components.CentreTopBar
import com.example.finwise.ui.navgraph.Graph
import com.example.finwise.util.Routes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


val BlueIconBackground = Color(0xFFE0EFFF)
val BlueIconTint = Color(0xFF007AFF)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    navController : NavHostController,

) {


    val processingState = viewModel.processingState
    val userProfile by viewModel.userProfileStateFlow.collectAsState()
    val firestoreUser by viewModel.firestoreUserStateFlow.collectAsState()


    var savedProfileBitmap by remember { mutableStateOf<Bitmap?>(null) }


    LaunchedEffect(userProfile?.imagePath) {
        val data = userProfile?.imagePath // Adjust field if name is different
        if (data != null) {
            withContext(Dispatchers.Default) {
                savedProfileBitmap = viewModel.byteArrayToBitmap(data)
            }
        } else {
            savedProfileBitmap = null
        }
    }

    Scaffold(
        topBar = {
            CentreTopBar(title = "Profile", navController = navController)
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->

        // Use Box for layering background shapes
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .padding(paddingValues)
                .background(Color(0xFF20c997)) // Base light background for the whole screen
        )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(100.dp)) // Space to push profile pic down

                // Profile Picture Section
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray) // Placeholder background
                        .border(4.dp, Color.White, CircleShape) // White border

                ) {
                    savedProfileBitmap?.let { bmp ->
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } ?: run {
                        // Placeholder Icon if no picture
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Default Profile Icon",
                            modifier = Modifier.size(80.dp),
                            tint = Color.Gray
                        )
                    }
                    // Loading Indicator over the picture
                    if (processingState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp), // Smaller indicator
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp
                        )
                    }
                }


                 //Name and ID
                Text(
                    // Adjust field name if needed (e.g., userProfile?.displayName)
                    text = firestoreUser?.lastName ?: "User Name",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    color = colorResource(id = R.color.letters_icons)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    // Adjust field name if needed (e.g., userProfile?.uid or a specific id field)
                    text = "ID: ${firestoreUser?.userId ?: "--------"}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    color = colorResource(id = R.color.letters_icons)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Action List (using Column for fixed items, LazyColumn if potentially long)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp), // Add horizontal padding for list items
                    verticalArrangement = Arrangement.spacedBy(16.dp) // Space between items
                ) {
                    ProfileListItem(
                        icon = painterResource(id = R.drawable.img_18),
                        text = "Edit Profile",
                        onClick = {
                            navController.navigate(Routes.EditProfileScreen)
                        }
                    )
                    ProfileListItem(
                        icon = painterResource(id = R.drawable.img_34),
                        text = "Security",

                    )
                    ProfileListItem(
                        icon = painterResource(id = R.drawable.img_33),
                        text = "Setting",

                    )

                    ProfileListItem(
                        icon = painterResource(id = R.drawable.img_32),
                        text = "Logout",
                        onClick = {
                            viewModel.logOut()
                            navController.navigate(Graph.Auth) {
                                popUpTo(Graph.Home) { inclusive = true }
                            }
                        } // Call ViewModel logout
                    )
                }

                // Display Error Message if processing/saving failed
                processingState.error?.let { errorMsg ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f)) // Push content up if screen is tall
            }
        }
    }


@Composable
fun ProfileListItem(
    icon: Painter,
    text: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp), // Add padding for better touch area
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon background
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp)) // Rounded corners for icon bg
                .background(BlueIconBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = text,
                tint = BlueIconTint,
                modifier = Modifier.size(24.dp) // Adjust icon size
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = FontFamily(Font(R.font.poppins_regular)),
            color = colorResource(id = R.color.letters_icons),
            fontWeight = FontWeight.ExtraBold
        )
    }
}