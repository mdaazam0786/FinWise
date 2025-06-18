package com.example.finwise.data.repository

import android.net.Uri
import android.util.Log
import com.example.finwise.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : UserRepository {
    override val currentUser get() = auth.currentUser

    override suspend fun signUpWithEmailAndPassword(email: String, password: String,fullName : String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserData(auth.currentUser?.uid ?: "", email, fullName)
                    // Handle successful registration
                    Log.d("UserRepository", "User registered successfully")
                } else {
                    // Handle registration failure
                    Log.e("UserRepository", "Registration failed: ${task.exception?.message}")
                }
            }
            .await()
    }

    private fun saveUserData(userId: String, email: String,fullName : String) {
        val userData = User(userId, email,fullName)
        val userRef = firestore.collection("users").document(userId)

        userRef.set(userData)
            .addOnSuccessListener {
                saveUserData(userId, email, fullName)
                // User data saved successfully
                Log.d("UserRepository", "User data saved successfully")
            }
            .addOnFailureListener {
                // Error occurred while saving user data
                Log.e("UserRepository", "Error saving user data: ${it.message}")
            }
    }
    override suspend fun sendEmailVerification() {
        currentUser?.sendEmailVerification()?.await()
    }

    override suspend fun logInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun getUser(userId: String): Result<User?> { // Or User?
        return try {
            val document = firestore.collection("users").document(userId)
                .get()
                .await() // Use await for coroutine suspension
            val user = document.toObject(User::class.java)
            Result.success(user) // Wrap in Result or return user directly
        } catch (e: Exception) {
            Log.e("USER_REPO", "Error fetching user", e)
            Result.failure(e) // Or return null
        }
    }


    override suspend fun updateUserEmail(userId: String, newEmail: String) : Result<Unit> {
        val user = auth.currentUser
        if (user == null || user.uid != userId) {
            // Cannot update if not logged in or incorrect user ID
            return Result.failure(IllegalStateException("User not logged in or mismatched ID."))
        }

        var emailUpdateSuccess = true

        try {
            // --- 1. Update Email in Firebase Auth (if changed) ---
            user.updateEmail(newEmail)
            firestore.collection("users").document(userId)
                .update("firstName", newEmail) // Use update for specific field
                .await()
            if (newEmail != user.email) {
                Log.d("USER_REPO", "Attempting to update email to: $newEmail")
                try {
                    Log.i("USER_REPO", "Firebase Auth email updated successfully.")

                } catch (e: FirebaseAuthRecentLoginRequiredException) {
                    Log.w("USER_REPO", "Email update failed: Requires recent login.", e)

                    return Result.failure(e) // Fail fast if re-auth is needed
                } catch (e: Exception) {
                    Log.e("USER_REPO", "Firebase Auth email update failed.", e)
                    emailUpdateSuccess = false
                }
            }

            // --- 3. Determine Overall Result ---
            return if (emailUpdateSuccess) {
                Result.success(Unit)
            } else {
                // Construct a meaningful error message if needed
                Result.failure(Exception("Failed to update profile. Email success: $emailUpdateSuccess"))
            }

        } catch (e: Exception) {
            // Catch any unexpected errors during the process
            Log.e("USER_REPO", "Unexpected error during user update.", e)
            return Result.failure(e)
        }
    }

    override suspend fun updateUserFullName(userId: String, newFullName: String): Result<Unit> {
        val user = auth.currentUser
        if (user == null || user.uid != userId) {
            // Cannot update if not logged in or incorrect user ID
            return Result.failure(IllegalStateException("User not logged in or mismatched ID."))
        }

        var fullNameUpdateSuccess = true

        Log.d("USER_REPO", "Attempting to update Firestore name to: $newFullName")
        try {
            // --- 1. Update Email in Firebase Auth (if changed) ---
            firestore.collection("users").document(userId)
                .update("lastName", newFullName) // Use update for specific field
                .await()
            Log.i("USER_REPO", "Firestore full name updated successfully.")

            // --- 3. Determine Overall Result ---
            return if (fullNameUpdateSuccess) {
                Result.success(Unit)
            } else {
                // Construct a meaningful error message if needed
                Result.failure(Exception("Failed to update profile. Email success: $fullNameUpdateSuccess"))
            }

        } catch (e: Exception) {
            // Catch any unexpected errors during the process
            Log.e("USER_REPO", "Unexpected error during user update.", e)
            return Result.failure(e)
        }

    }


    override suspend fun deleteUser() {
        currentUser?.delete()?.await()
    }

    override suspend fun reloadUser() {
        currentUser?.reload()?.await()
    }


    override suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override fun signOut() = auth.signOut()

    override fun getAuthState() = callbackFlow {
        val authStateListener = AuthStateListener { auth ->
            trySend(auth.currentUser == null)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }
}