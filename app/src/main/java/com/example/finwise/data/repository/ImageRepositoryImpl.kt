package com.example.finwise.data.repository

import com.example.finwise.data.dao.ImageDao
import com.example.finwise.data.model.image.UserImage
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Singleton
class ImageRepositoryImpl @Inject constructor(
    private val imageDao: ImageDao
) : ImageRepository {

    override fun getUserProfileFlow(): Flow<UserImage?> {
        // Room handles the thread for Flow observation
        return imageDao.getUserProfileFlow("user001")
    }

    override suspend fun updateUserProfilePicture(imageData: ByteArray) {
        withContext(Dispatchers.IO) {
            // Get current user or create a new one if it doesn't exist
            val currentUser = imageDao.getUserProfile("user001")
            val updatedUser = currentUser?.copy(imagePath = imageData)
                ?: UserImage(id = "user001", imagePath = imageData) // Create if null

            imageDao.insertImage(updatedUser)
        }
    }
}