package com.example.finwise.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.finwise.data.dao.ImageDao
import com.example.finwise.data.model.image.UserImage
import com.example.finwise.util.Converters
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@Database(entities = [UserImage::class], version = 3)
@Singleton
abstract class ImageDatabase : RoomDatabase(){
     abstract fun imageDao(): ImageDao

     companion object{
         const val DATABASE_NAME = "image_db"

         fun getInstance(@ApplicationContext context: Context): ImageDatabase{
             return Room.databaseBuilder(
                 context.applicationContext,
                 ImageDatabase::class.java,
                 DATABASE_NAME
             )
                 .fallbackToDestructiveMigration()
                 .build()
         }
     }
}