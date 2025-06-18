package com.example.finwise.ui.auth.login2

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finwise.data.repository.UserRepository
import com.example.finwise.util.Routes
import com.example.finwise.util.Routes.EMPTY_STRING
import com.example.finwise.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias SignInResponse = com.example.finwise.util.Result<Unit>
@HiltViewModel
class LoginViewModel2 @Inject constructor(
    private val repository: UserRepository
): ViewModel() {

    private val _uiEvent =  Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    private val _email = MutableStateFlow(EMPTY_STRING)
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow(EMPTY_STRING)
    val password: StateFlow<String> = _password.asStateFlow()


    private val _signInState = MutableStateFlow<SignInResponse>(com.example.finwise.util.Result.Idle)
    val signInState: StateFlow<SignInResponse> = _signInState.asStateFlow()


    fun onLoginEvent2(event : LoginEvent2){
        when(event){
            LoginEvent2.onFacebookButtonClick -> TODO()
            LoginEvent2.onForgotPasswordButtonClick -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.ForgetPasswordScreen))
                }
            }
            LoginEvent2.onGoogleButtonClick -> TODO()
            LoginEvent2.onLoginButtonClick -> {
                loginWithEmailAndPassword(_email.value, _password.value)
            }
            LoginEvent2.onSignupButtonClick -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.SignupScreen))
                }
            }
            is LoginEvent2.onEmailChange -> {
                viewModelScope.launch {
                    onEmailChange(event.email)
                }
            }
            is LoginEvent2.onPasswordChange -> {
                viewModelScope.launch {
                    onPasswordChange(event.password)
                }
            }
        }
    }

    private fun loginWithEmailAndPassword(email: String, password: String) = viewModelScope.launch {
        try {
            _signInState.value = com.example.finwise.util.Result.Loading
            _signInState.value = com.example.finwise.util.Result.Success(
                repository.logInWithEmailAndPassword(
                    email,
                    password
                )
            )
            sendUiEvent(UiEvent.Navigate(Routes.HomeScreen))
        } catch (e: Exception) {
            _signInState.value = com.example.finwise.util.Result.Error(e)
        }
    }
    private fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    private fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }
    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}