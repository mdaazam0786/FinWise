package com.example.finwise.data.repository

import android.net.Uri
import com.example.finwise.data.User
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val currentUser: FirebaseUser?

    suspend fun signUpWithEmailAndPassword(email: String, password: String,fullName : String)

    suspend fun sendEmailVerification()

    suspend fun logInWithEmailAndPassword(email: String, password: String)

    suspend fun deleteUser()

    suspend fun reloadUser()

    suspend fun getUser(userId: String) : Result<User?>

    suspend fun updateUserEmail(userId : String, newEmail: String) : Result<Unit>

    suspend fun updateUserFullName(userId: String, newFullName: String) : Result<Unit>

    suspend fun sendPasswordResetEmail(email: String)

    fun signOut()

    fun getAuthState(): Flow<Boolean>
}