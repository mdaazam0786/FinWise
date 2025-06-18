package com.example.finwise.ui.home.profile.editprofile

data class EditProfileState (
    val loading : Boolean = false,
    val error : String? = null,
    val userId : String? = null,
    val initialImage : ByteArray? = null,
    val initialName : String? = null,
    val initialId : String? = null,
    val initialEmail : String? = null,
    val saveInProgress: Boolean = false,
)