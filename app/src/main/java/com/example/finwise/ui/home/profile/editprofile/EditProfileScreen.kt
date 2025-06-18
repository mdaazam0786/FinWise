package com.example.finwise.ui.home.profile.editprofile

// Add necessary imports if missing
import BottomNavigationBar // Assuming this is in the correct package
import android.graphics.Bitmap
import android.net.Uri // Needed for observing state
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest // Use modern picker request
import androidx.activity.result.contract.ActivityResultContracts // Use modern picker contract
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage // Import Coil AsyncImage
import coil.request.ImageRequest
import com.example.finwise.R // Assuming you have error strings in strings.xml
import com.example.finwise.ui.components.CentreTopBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun EditProfileScreen(
    navController : NavHostController,
    viewModel : EditProfileViewModel = hiltViewModel()
) {
    // --- State Observation ---
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val firestoreUser by viewModel.firestoreUserStateFlow.collectAsStateWithLifecycle()
    val savedUserImage by viewModel.userProfileStateFlow.collectAsStateWithLifecycle() // Observe the saved image data object
    val selectedImageUri by viewModel.selectedImageUri.collectAsStateWithLifecycle() // Observe the NEWLY selected URI
    val fullName by viewModel.fullName.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // State to hold the bitmap converted from the SAVED byte array
    var savedProfileBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // --- Effects ---

    // Effect to convert SAVED image ByteArray to Bitmap when it changes
    LaunchedEffect(savedUserImage) {
        val data = savedUserImage?.imagePath // Get the byte array
        Log.d("EditProfileScreen", "SavedUserImage changed. Data present: ${data != null}")
        if (data != null) {
            // Use the VM function which runs on IO dispatcher
            savedProfileBitmap = viewModel.byteArrayToBitmap(data)
            Log.d("EditProfileScreen", "Converted saved ByteArray to Bitmap: ${savedProfileBitmap != null}")
        } else {
            savedProfileBitmap = null // Clear bitmap if no saved image data
            Log.d("EditProfileScreen", "No saved image data, cleared savedProfileBitmap.")
        }
    }

    // Effect to show snackbar on error
    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMessage ->
            Log.d("EditProfileScreen", "Showing snackbar for error: $errorMessage")
            snackbarHostState.showSnackbar(
                message = errorMessage,
                duration = androidx.compose.material3.SnackbarDuration.Short
            )
            // TODO: Consider adding a mechanism in VM to clear the error after showing
        }
    }

    // --- Activity Result Launcher ---
    // Use the modern PickVisualMedia contract
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            // Pass the result URI (can be null) to the ViewModel
            viewModel.onImageSelected(uri)
            Log.d("EditProfileScreen", "Photo picker result URI: $uri")
        }
    )

    // --- UI ---
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Add SnackbarHost
        topBar = {
            CentreTopBar(title = "Edit Profile", navController = navController) // Title adjusted
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(Color(0xFF20c997)) // Base light background for the whole screen
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
                .verticalScroll(rememberScrollState()) // Make column scrollable
                .padding(horizontal = 16.dp), // Add horizontal padding for content
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            // --- Profile Picture Section ---
            Box(
                contentAlignment = Alignment.Center // Center content (icon/image)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray) // Use theme color
                        .border(4.dp, Color.White, CircleShape) // Use theme color
                        .clickable(enabled = !uiState.saveInProgress) { // Disable click during save
                            Log.d("EditProfileScreen", "Profile picture clicked, launching picker.")
                            // Launch the modern photo picker asking only for Images
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Display Priority: New Selection > Saved Image > Placeholder
                    when {
                        // 1. If a new image URI is selected, display it using Coil
                        selectedImageUri != null -> {
                            Log.d("EditProfileScreen", "Displaying selectedImageUri using Coil.")
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(selectedImageUri)
                                    .crossfade(true) // Add smooth transition
                                    .error(R.drawable.ic_launcher_background) // Placeholder for load error
                                    .build(),
                                contentDescription = "Selected profile picture preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop // Crop to fit the circle
                            )
                        }
                        // 2. If no new image, but a saved image bitmap exists, display it
                        savedProfileBitmap != null -> {
                            Log.d("EditProfileScreen", "Displaying savedProfileBitmap.")
                            Image(
                                bitmap = savedProfileBitmap!!.asImageBitmap(), // Use non-null assertion as it's checked
                                contentDescription = "Saved profile picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        // 3. Otherwise, show the default placeholder icon
                        else -> {
                            Log.d("EditProfileScreen", "Displaying placeholder icon.")
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Default Profile Icon",
                                modifier = Modifier.size(80.dp), // Adjust size as needed
                                tint = MaterialTheme.colorScheme.onSurfaceVariant // Use theme color
                            )
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            // --- Name and ID Display ---
            // Use data from firestoreUser state
            Text(
                text = firestoreUser?.lastName ?: "Loading Name...", // Use placeholder text
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ID: ${firestoreUser?.userId ?: "Loading ID..."}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Use theme color
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Input Fields ---
            // Full Name
            OutlinedTextField(
                value = fullName,
                onValueChange = { viewModel.onEditProfileEvent(EditProfileEvent.OnNameChange(it)) },
                label = { Text("Full Name") }, // Use label instead of separate Text
                placeholder = { Text(firestoreUser?.lastName ?: "Enter full name") }, // Placeholder inside
                shape = RoundedCornerShape(12.dp), // Slightly less rounded
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors( // Customize colors if needed
                    // focusedBorderColor = ...,
                    // unfocusedBorderColor = ...,
                ),
                singleLine = true,
                enabled = !uiState.saveInProgress // Disable during save
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onEditProfileEvent(EditProfileEvent.OnEmailChange(it)) },
                label = { Text("Email") },
                placeholder = { Text(firestoreUser?.firstName ?: "Enter email") }, // Placeholder inside
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(),
                singleLine = true,
                enabled = !uiState.saveInProgress // Disable during save
                // Consider adding keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Save Button ---
            Button(
                onClick = {
                    Log.d("EditProfileScreen", "Save Changes button clicked.")
                    viewModel.saveChanges()
                    // viewModel.onEditProfileEvent(EditProfileEvent.OnSubmit) // Alternative way to call
                },
                modifier = Modifier.fillMaxWidth(0.8f), // Make button slightly less wide
                enabled = !uiState.saveInProgress // Disable button when saving
            ) {
                if (uiState.saveInProgress) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary, // Spinner color
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.size(8.dp)) // Space between spinner and text
                    Text(text = "Saving...")
                } else {
                    Text(text = "Save Changes")
                }
            }
            Spacer(modifier = Modifier.height(16.dp)) // Bottom spacing
        }
    }
}