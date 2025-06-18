package com.example.finwise.data

data class FirebaseAuthState(
    val isLoading: Boolean = false,
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
