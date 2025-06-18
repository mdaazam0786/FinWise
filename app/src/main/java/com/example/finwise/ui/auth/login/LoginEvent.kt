package com.example.finwise.ui.auth.login

sealed class LoginEvent {
    object onLoginButtonClick : LoginEvent()
    object onSignupButtonClick : LoginEvent()
    object onForgetPasswordButtonClick : LoginEvent()
}