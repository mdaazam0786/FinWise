package com.example.finwise.ui.home.profile.editprofile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finwise.data.User
import com.example.finwise.data.model.image.UserImage
import com.example.finwise.data.repository.ImageRepository
import com.example.finwise.data.repository.UserRepository
// Remove unused imports if any after changes
import com.example.finwise.util.Routes
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.math.roundToInt


private const val MAX_IMAGE_DIMENSION = 1024
private const val COMPRESSION_QUALITY = 85


@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val auth : FirebaseAuth,
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileState())
    val uiState: StateFlow<EditProfileState> = _uiState.asStateFlow()

    private val _fullName = MutableStateFlow(Routes.EMPTY_STRING)
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _email = MutableStateFlow(Routes.EMPTY_STRING)
    val email: StateFlow<String> = _email.asStateFlow()

    // --- State for the NEWLY SELECTED image URI ---
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    // --- REMOVED selectedImageBitmapPreview ---
    // The UI will now load the preview directly from selectedImageUri using Coil

    // State for the CURRENTLY SAVED user profile image (from repository)
    val userProfileStateFlow: StateFlow<UserImage?> = imageRepository.getUserProfileFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null // Initially null until loaded
        )

    // State for the Firestore user data (name, id, etc.)
    private val _firestoreUserState = MutableStateFlow<User?>(null)
    val firestoreUserStateFlow: StateFlow<User?> = _firestoreUserState.asStateFlow()

    private val userId = auth.currentUser?.uid ?: ""


    init {
        fetchFireStoreUser()

    }

    // --- Data Loading ---

    private fun fetchFireStoreUser() {
        viewModelScope.launch {
            if (userId.isEmpty()) {
                Log.e("PROFILE_VM", "Cannot fetch user data: User ID is empty.")
                _uiState.update { it.copy(error = "User not identified.") }
                return@launch
            }
            val result = userRepository.getUser(userId)
            result.onSuccess { user ->
                _firestoreUserState.value = user
                // --- Initialize fields with fetched data ---
                _fullName.value = user?.lastName ?: "" // Assuming lastName is full name
                _email.value = user?.firstName ?: ""  // Assuming firstName is email (Adjust as needed)
                _uiState.update { it.copy(
                    initialName = user?.lastName,
                    initialEmail = user?.firstName // Store initial values for comparison on save
                )}
                Log.d("PROFILE_VM", "Firestore user fetched: ${user?.email}")
            }.onFailure { exception ->
                Log.e("PROFILE_VM", "Failed to fetch Firestore user", exception)
                _uiState.update { it.copy(error = "Failed to load user data.") }
            }
        }
    }

    // --- Event Handling ---

    @RequiresApi(Build.VERSION_CODES.R)
    fun onEditProfileEvent(event: EditProfileEvent){
        when(event){
            is EditProfileEvent.OnEmailChange -> onEmailChange(event.email)
            is EditProfileEvent.OnNameChange -> onFullNameChange(event.fullName)
            EditProfileEvent.OnSubmit -> saveChanges() // Trigger save logic
        }
    }

    /**
     * Called from the UI when the user selects an image using the picker.
     * Updates the selectedImageUri state.
     */
    fun onImageSelected(uri: Uri?) {
        _selectedImageUri.value = uri
        // No need to load bitmap here anymore, UI will handle preview with Coil
        Log.d("PROFILE_VM", "New image URI selected: $uri")
    }

    private fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    private fun onFullNameChange(newFullName: String) {
        _fullName.value = newFullName
    }

    // --- Save Logic ---

    @RequiresApi(Build.VERSION_CODES.R)
    fun saveChanges() {
        if (uiState.value.saveInProgress) return

        val currentUserId = userId // Use the userId fetched during init
        if (currentUserId.isEmpty()) {
            Log.e("EditProfileVM", "Cannot save: User ID is missing.")
            _uiState.update { it.copy(error = "Cannot save, user not identified.") }
            return
        }

        val initialName = _uiState.value.initialName ?: "" // Use stored initial value
        val initialEmail = _uiState.value.initialEmail ?: "" // Use stored initial value

        // Determine what changed
        val nameChanged = _fullName.value != initialName && _fullName.value.isNotEmpty()
        // NOTE: Email change handling might require re-authentication or verification,
        // which is complex. For this example, we'll assume you just update the Firestore field.
        // Be cautious about allowing direct email field changes without proper verification.
        val emailChanged = _email.value != initialEmail && _email.value.isNotEmpty()
        val imageChanged = _selectedImageUri.value != null // A new image URI was selected

        if (!nameChanged && !emailChanged && !imageChanged) {
            Log.d("EditProfileVM", "No changes to save.")
            _uiState.update { it.copy(saveInProgress = false, error = null) } // Clear any previous error/progress
            return
        }

        Log.d("EditProfileVM", "Starting saveChanges. Name changed: $nameChanged, Email changed: $emailChanged, Image changed: $imageChanged")
        _uiState.update { it.copy(saveInProgress = true, error = null) }

        viewModelScope.launch {
            var saveFailed = false
            var errorMessage: String? = null

            try {
                // --- Process and Save Image ONLY if changed ---
                if (imageChanged) {
                    Log.d("EditProfileVM", "Processing selected image for save...")
                    val processedBytes = processNewImage(_selectedImageUri.value) // Process the stored URI
                    if (processedBytes != null) {
                        Log.d("EditProfileVM", "Image processed for save. Size: ${processedBytes.size}. Calling repo to update image...")
                        // Assuming repo function handles success/failure internally or returns Result
                        // TODO: Add error handling if repo function can fail visibly
                        imageRepository.updateUserProfilePicture(processedBytes)
                        Log.d("EditProfileVM", "Image update call finished.")
                    } else {
                        Log.e("EditProfileVM", "Image processing failed.")
                        saveFailed = true
                        errorMessage = "Failed to process new image."
                    }
                }

                // --- Save Name (if changed and image processing didn't fail) ---
                if (!saveFailed && nameChanged) {
                    Log.d("EditProfileVM", "Calling repo to update name to: ${_fullName.value}")
                    val nameUpdateResult = userRepository.updateUserFullName(currentUserId, _fullName.value)
                    if (nameUpdateResult.isFailure) {
                        Log.e("EditProfileVM", "Name update failed", nameUpdateResult.exceptionOrNull())
                        saveFailed = true
                        errorMessage = "Failed to update name."
                    } else {
                        Log.d("EditProfileVM", "Name update successful.")
                    }
                }

                // --- Save Email (if changed and previous steps didn't fail) ---
                // WARNING: See note above about email change complexity. This just updates Firestore.
                if (!saveFailed && emailChanged) {
                    Log.d("EditProfileVM", "Calling repo to update email to: ${_email.value}")
                    // You might need a specific function like updateUserEmail in your repository
                    // Assuming updateUserFields or similar can handle it:
                    val emailUpdateResult = userRepository.updateUserEmail(currentUserId, _email.value) // ADJUST REPO CALL
                    if (emailUpdateResult.isFailure) {
                        Log.e("EditProfileVM", "Email update failed", emailUpdateResult.exceptionOrNull())
                        saveFailed = true
                        errorMessage = "Failed to update email."
                    } else {
                        Log.d("EditProfileVM", "Email update successful.")
                    }
                }

                // --- Final State Update ---
                if (!saveFailed) {
                    Log.d("EditProfileVM", "Save successful!")
                    _uiState.update { it.copy(
                        saveInProgress = false,
                        error = null,
                        // Update initial values to reflect saved state ONLY on success
                        initialName = if (nameChanged) _fullName.value else it.initialName,
                        initialEmail = if (emailChanged) _email.value else it.initialEmail
                    ) }
                    _selectedImageUri.value = null // Clear selection state after successful save
                    // Potentially trigger navigation or show success message via another state/event
                } else {
                    Log.e("EditProfileVM", "Save failed. Error: $errorMessage")
                    _uiState.update { it.copy(saveInProgress = false, error = errorMessage) }
                }

            } catch (e: Exception) {
                Log.e("EditProfileVM", "Error during save process", e)
                _uiState.update { it.copy(saveInProgress = false, error = "An unexpected error occurred during save.") }
            }
        }
    }


    // --- Image Processing ---

    @RequiresApi(Build.VERSION_CODES.R)
    private suspend fun processNewImage(uri: Uri?): ByteArray? {
        uri ?: return null
        val originalBytes = convertUriToFullByteArray(uri)
        originalBytes ?: return null
        Log.d("PROFILE_VM", "processNewImage: Original size = ${originalBytes.size}")
        val processedBytes = resizeAndCompressImage(originalBytes)
        Log.d("PROFILE_VM", "processNewImage: Processed size = ${processedBytes?.size}")
        return processedBytes
    }

    private suspend fun convertUriToFullByteArray(uri: Uri): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.readBytes()
                }
            } catch (e: Exception) {
                Log.e("PROFILE_VM", "Error reading bytes from URI $uri", e)
                null
            }
        }
    }

    // ---- Image Processing Helper Functions - Keep these ----
    @RequiresApi(Build.VERSION_CODES.R)
    private suspend fun resizeAndCompressImage(originalByteArray: ByteArray): ByteArray? {
        // ... (implementation remains the same - provided in your code)
        return withContext(Dispatchers.Default) {
            try {
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeByteArray(originalByteArray, 0, originalByteArray.size, options)
                options.inSampleSize = calculateInSampleSize(options, MAX_IMAGE_DIMENSION, MAX_IMAGE_DIMENSION )
                options.inJustDecodeBounds = false
                val bitmap = BitmapFactory.decodeByteArray(originalByteArray, 0, originalByteArray.size, options)
                    ?: run { Log.e("RESIZE_COMPRESS", "decodeByteArray returned null"); return@withContext null }
                val scaledBitmap = scaleBitmap(bitmap, MAX_IMAGE_DIMENSION)
                if (scaledBitmap != bitmap) bitmap.recycle() // Recycle original if scaled
                ByteArrayOutputStream().use { outputStream ->
                    // Use WEBP for potentially better compression/quality balance, fallback to JPEG
                    val format = Bitmap.CompressFormat.WEBP_LOSSY // Or WEBP_LOSSLESS, JPEG
                    val success = scaledBitmap?.compress(format, COMPRESSION_QUALITY, outputStream) ?: false
                    scaledBitmap?.recycle() // Recycle scaled bitmap
                    if (success && outputStream.size() > 0) {
                        Log.d("RESIZE_COMPRESS", "Compressed size: ${outputStream.size()} bytes using $format")
                        outputStream.toByteArray()
                    } else { Log.e("RESIZE_COMPRESS", "Compression failed or empty output stream"); null }
                }
            } catch (e: OutOfMemoryError) { Log.e("RESIZE_COMPRESS", "OOM!", e); null }
            catch (e: Exception) { Log.e("RESIZE_COMPRESS", "Error resizing/compressing!", e); null }
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // ... (implementation remains the same - provided in your code)
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        Log.d("RESIZE_COMPRESS", "Calculated inSampleSize: $inSampleSize")
        return inSampleSize
    }

    private fun scaleBitmap(bitmap: Bitmap?, maxDimension: Int): Bitmap? {
        // ... (implementation remains the same - provided in your code)
        bitmap ?: return null
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) return bitmap
        val scaleFactor = if (width > height) maxDimension.toFloat() / width else maxDimension.toFloat() / height
        val newWidth = (width * scaleFactor).roundToInt()
        val newHeight = (height * scaleFactor).roundToInt()
        // Ensure dimensions are at least 1
        val finalWidth = newWidth.coerceAtLeast(1)
        val finalHeight = newHeight.coerceAtLeast(1)
        Log.d("RESIZE_COMPRESS", "Scaling bitmap from ${width}x${height} to ${finalWidth}x${finalHeight}")
        return try { Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true) }
        catch (e: OutOfMemoryError) { Log.e("RESIZE_COMPRESS", "OOM scaling bitmap!", e); null }
        catch (e: Exception) { Log.e("RESIZE_COMPRESS", "Error scaling bitmap!", e); null }
    }

    /**
     * Converts the saved ByteArray (from UserImage) to a Bitmap for display.
     * Called from the Screen's LaunchedEffect.
     */
    suspend fun byteArrayToBitmap(byteArray: ByteArray?): Bitmap? {
        // This function is now primarily for displaying the *saved* image
        return withContext(Dispatchers.IO) { // Ensure decoding happens off the main thread
            byteArray?.let {
                if (it.isEmpty()) return@let null
                try {
                    BitmapFactory.decodeByteArray(it, 0, it.size)
                } catch (e: IllegalArgumentException) {
                    Log.e("PROFILE_VM", "Decode failed for saved byte array", e); null
                } catch (e: OutOfMemoryError) {
                    Log.e("PROFILE_VM", "OOM decoding saved byte array", e); null
                }
            }
        }
    }
}