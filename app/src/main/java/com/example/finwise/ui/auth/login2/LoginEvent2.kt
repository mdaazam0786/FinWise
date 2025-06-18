package com.example.finwise.ui.auth.login2

import androidx.compose.ui.text.input.TextFieldValue
import org.w3c.dom.Text

sealed class LoginEvent2 {
    object onLoginButtonClick : LoginEvent2()
    object onSignupButtonClick : LoginEvent2()
    object onForgotPasswordButtonClick : LoginEvent2()
    object onFacebookButtonClick : LoginEvent2()
    object onGoogleButtonClick : LoginEvent2()
    data class onEmailChange(val email: String) : LoginEvent2()
    data class onPasswordChange(val password: String) : LoginEvent2()

}