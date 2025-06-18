package com.example.finwise.ui.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finwise.data.repository.UserRepository
import com.example.finwise.util.Result
import com.example.finwise.util.Routes
import com.example.finwise.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias signupResponse = Result<Unit>

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    private val _uiEvent =  Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    private val _email = MutableStateFlow(Routes.EMPTY_STRING)
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow(Routes.EMPTY_STRING)
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow(Routes.EMPTY_STRING)
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()


    private val _mobileNumber = MutableStateFlow(Routes.EMPTY_STRING)
    val mobileNumber: StateFlow<String> = _mobileNumber.asStateFlow()

    private val _fullName = MutableStateFlow(Routes.EMPTY_STRING)
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _dateOfBirth = MutableStateFlow(Routes.EMPTY_STRING)
    val dateOfBirth: StateFlow<String> = _dateOfBirth.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _signUpState = MutableStateFlow<signupResponse>(Result.Idle)
    val signUpState: StateFlow<signupResponse> = _signUpState.asStateFlow()


    fun onSignupEvent(event : SignupEvent){
        when(event){
            is SignupEvent.OnDateOfBirth -> {
                viewModelScope.launch {
                    onDateOfBirthChange(event.newDateOfBirth)
                }
            }
            is SignupEvent.OnEmailChanged -> {
                viewModelScope.launch {
                    onEmailChange(event.newEmail)
                }
            }
            is SignupEvent.OnFullNameChanged -> {
                viewModelScope.launch {
                    onFullNameChange(event.newFullName)
                }
            }
            is SignupEvent.OnMobileNumber -> {
                viewModelScope.launch {
                    onMobileNumberChange(event.newMobileNumber)
                }
            }
            is SignupEvent.OnPasswordChanged -> {
                viewModelScope.launch {
                    onPasswordChange(event.newPassword)
                }
            }
            is SignupEvent.OnConfirmPasswordChanged -> {
                viewModelScope.launch {
                    onConfirmPasswordChange(event.newConfirmPassword)
                }
            }
            SignupEvent.SignUpClicked -> {
                signUpWithEmailAndPassword(_email.value, _password.value, _fullName.value)
            }
        }
    }

    private fun onFullNameChange(newFullName: String) {
        _fullName.value = newFullName
    }

    private fun onDateOfBirthChange(newDateOfBirth: String) {
        _dateOfBirth.value = newDateOfBirth
    }

    private fun onMobileNumberChange(newMobileNumber: String) {
        _mobileNumber.value = newMobileNumber
    }

    private fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    private fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }
    private fun onConfirmPasswordChange(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
    }
    private fun signUpWithEmailAndPassword(email: String, password: String,fullName : String) = viewModelScope.launch {
        _isLoading.value = true
        try {
            _signUpState.value = Result.Loading
            _signUpState.value = Result.Success(repository.signUpWithEmailAndPassword(email, password, fullName))

            sendUiEvent(UiEvent.Navigate(Routes.HomeScreen))
        } catch (e: Exception) {
            _signUpState.value = Result.Error(e)
            _isLoading.value = false
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}