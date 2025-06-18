package com.example.finwise.ui.home.profile.editprofile

sealed class EditProfileEvent {
    data class OnNameChange(val fullName : String) : EditProfileEvent()
    data class OnEmailChange(val email : String) : EditProfileEvent()
    object OnSubmit : EditProfileEvent()

}