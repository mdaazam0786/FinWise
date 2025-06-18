package com.example.finwise.ui.home.profile // Ensure this package matches yours

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finwise.data.model.image.UserImage // Use your actual Entity class here
import com.example.finwise.data.repository.ImageRepository // Assuming this handles image DB operations
import com.example.finwise.data.repository.UserRepository // For user operations like logout
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.math.roundToInt
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.example.finwise.data.User
import com.example.finwise.util.Routes
import com.example.finwise.util.UiEvent
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

// Simplified state, primarily for loading/error during the immediate save process
data class ProcessingState(
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth : FirebaseAuth,
    private val repository: UserRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {


    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    // State for the image processing/saving triggered by selection
    var processingState by mutableStateOf(ProcessingState())
        private set

    private val _firestoreUserState = MutableStateFlow<User?>(null)
    val firestoreUserStateFlow: StateFlow<User?> = _firestoreUserState.asStateFlow()

    // StateFlow observing the SAVED user profile image from the database (Remains the same)
    val userProfileStateFlow: StateFlow<UserImage?> = imageRepository.getUserProfileFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        fetchFireStoreUser() // Fetch user data when ViewModel is created
    }


    private fun fetchFireStoreUser() {
        val currentUserId = auth.currentUser?.uid // Get current user ID
        if (currentUserId != null) {
            viewModelScope.launch {
                // Use Result wrapper approach
                val result = repository.getUser(currentUserId)
                result.onSuccess { user ->
                    _firestoreUserState.value = user
                    Log.d("PROFILE_VM", "Firestore user fetched: ${user?.email}")
                }.onFailure { exception ->
                    Log.e("PROFILE_VM", "Failed to fetch Firestore user", exception)
                    // Optionally set an error state here
                }
            }
        } else {
            Log.w("PROFILE_VM", "Cannot fetch Firestore user: No current user logged in.")
            // Handle case where user is not logged in (e.g., clear state or show error)
            _firestoreUserState.value = null
        }
    }


    fun byteArrayToBitmap(byteArray: ByteArray?): Bitmap? {
        // ... (implementation remains the same)
        return byteArray?.let {
            if (it.isEmpty()) return@let null
            try { BitmapFactory.decodeByteArray(it, 0, it.size) }
            catch (e: IllegalArgumentException) { Log.e("PROFILE_VM", "Decode failed", e); null }
            catch (e: OutOfMemoryError) { Log.e("PROFILE_VM", "OOM decoding", e); null }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
        // Logout function - Keep this
        fun logOut() {
            // ... (implementation remains the same)
            viewModelScope.launch {
                repository.signOut()
                _firestoreUserState.value = null
                Log.d("PROFILE_VM", "Logout initiated.")
            }
        }

}