package com.example.finwise.data.dao

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.finwise.data.model.image.UserImage
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertImage(image : UserImage)

    // Get the user profile reactively. Returns null if no profile exists.
    @Query("SELECT * FROM user_image WHERE id = :userId LIMIT 1")
    fun getUserProfileFlow(userId: String): Flow<UserImage?>

    // Optional: Get the profile once (non-Flow)
    @Query("SELECT * FROM user_image WHERE id = :userId LIMIT 1")
    suspend fun getUserProfile(userId: String): UserImage?

}