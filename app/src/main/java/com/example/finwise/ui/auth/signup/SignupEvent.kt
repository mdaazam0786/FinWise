package com.example.finwise.ui.auth.signup

sealed class SignupEvent {
    data class OnFullNameChanged(val newFullName: String) : SignupEvent()
    data class OnEmailChanged(val newEmail: String) : SignupEvent()
    data class OnPasswordChanged(val newPassword: String) : SignupEvent()
    data class OnConfirmPasswordChanged(val newConfirmPassword: String) : SignupEvent()
    data class OnMobileNumber(val newMobileNumber: String) : SignupEvent()
    data class OnDateOfBirth(val newDateOfBirth: String) : SignupEvent()
    object SignUpClicked : SignupEvent()

}