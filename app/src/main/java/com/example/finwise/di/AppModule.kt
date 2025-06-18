package com.example.finwise.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.finwise.data.dao.ExpenseDao
import com.example.finwise.data.dao.ImageDao
import com.example.finwise.data.dao.IncomeDao
import com.example.finwise.data.dao.SavingsDao
import com.example.finwise.data.database.ExpenseDatabase
import com.example.finwise.data.database.ImageDatabase
import com.example.finwise.data.database.IncomeDatabase
import com.example.finwise.data.database.SavingsDatabase
import com.example.finwise.data.model.savings.Savings
import com.example.finwise.data.repository.ImageRepository
import com.example.finwise.data.repository.ImageRepositoryImpl
import com.example.finwise.data.repository.TransactionRepository
import com.example.finwise.data.repository.UserRepository
import com.example.finwise.data.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton



@Module
@InstallIn(ViewModelComponent::class)
class AppModule {
    @Provides
    @ViewModelScoped
    fun provideFirebaseAuth() : FirebaseAuth {
        lateinit var auth: FirebaseAuth
        auth = Firebase.auth
        return auth
    }

    @Provides
    @ViewModelScoped
    fun provideFirebaseFireStore() : FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @ViewModelScoped
    fun provideFirebaseStorage() : FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @ViewModelScoped
    fun provideUserRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
    ): UserRepository = UserRepositoryImpl(
        auth = auth,
        firestore = firestore,
    )

    @Provides
    @ViewModelScoped
    fun provideImageRepository(
        imageDao: ImageDao
    ) : ImageRepository = ImageRepositoryImpl(
        imageDao = imageDao
    )

    @Provides
    @ViewModelScoped
    fun provideTransactionRepository(
        incomeDao : IncomeDao,
        expenseDao: ExpenseDao
    ) : TransactionRepository = TransactionRepository(
       incomeDao = incomeDao,
        expenseDao = expenseDao
    )


    @Provides
    fun provideExpenseDatabase(@ApplicationContext context: Context) : ExpenseDatabase{
        return ExpenseDatabase.getInstance(context)
    }

    @Provides
    fun provideExpenseDao(expenseDatabase: ExpenseDatabase) : ExpenseDao{
        return expenseDatabase.expenseDao()
    }

    @Provides
    fun provideIncomeDatabase(@ApplicationContext context: Context) : IncomeDatabase{
        return IncomeDatabase.getInstance(context)
    }

    @Provides
    fun provideIncomeDao(incomeDatabase: IncomeDatabase) : IncomeDao{
        return incomeDatabase.incomeDao()
    }

    @Provides
    fun provideSavingsDatabase(@ApplicationContext context: Context) : SavingsDatabase {
        return SavingsDatabase.getInstance(context)
    }

    @Provides
    fun provideSavingsDao(savingsDatabase: SavingsDatabase) : SavingsDao {
        return savingsDatabase.savingsDao()
    }

    @Provides
    fun provideImageDatabase(@ApplicationContext context: Context) : ImageDatabase{
        return ImageDatabase.getInstance(context)
    }

    @Provides
    fun provideImageDao(imageDatabase: ImageDatabase) : ImageDao{
        return imageDatabase.imageDao()
    }

}