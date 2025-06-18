package com.example.finwise.data.repository

import com.example.finwise.data.model.image.UserImage
import kotlinx.coroutines.flow.Flow

interface ImageRepository {

    fun getUserProfileFlow(): Flow<UserImage?>

    suspend fun updateUserProfilePicture(imageData: ByteArray)
}