package com.example.finwise.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finwise.util.Routes
import com.example.finwise.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {
    private val _uiEvent =  Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onLoginEvent(event : LoginEvent){
        when(event){
            LoginEvent.onForgetPasswordButtonClick -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.ForgetPasswordScreen))
                }
            }
            LoginEvent.onLoginButtonClick -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.LoginScreen2))
                }
            }
            LoginEvent.onSignupButtonClick -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.SignupScreen))
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}