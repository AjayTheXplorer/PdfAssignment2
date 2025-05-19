package com.example.pdfassignment2.di

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.savedstate.SavedStateRegistryOwner
import com.example.pdfassignment2.model.apiService.FcmService
import com.example.pdfassignment2.model.apiService.ProductApiService
import com.example.pdfassignment2.model.localDB.dao.ProductDao
import com.example.pdfassignment2.model.localDB.dao.UserDao
import com.example.pdfassignment2.model.localDB.db.AppDatabase
import com.example.pdfassignment2.model.localDB.db.UserDatabase
import com.example.pdfassignment2.preferences.PreferencesManager
import com.example.pdfassignment2.repository.FcmRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserDatabase(@ApplicationContext context: Context): UserDatabase {
        return Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            "user_database"
        ).fallbackToDestructiveMigration().build()// Add a fallback
    }


    @Provides
    fun provideUserDao(userDatabase: UserDatabase): UserDao {
        return userDatabase.userDao()
    }


    @Provides
    @Singleton
    fun provideProductDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "product_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    @Singleton
    fun provideProductApi(): ProductApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.restful-api.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductApiService::class.java)
    }


//    @Provides
//    @Singleton
//    fun provideRetrofit(): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl("https://fcm.googleapis.com/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideFcmService(retrofit: Retrofit): FcmService {
//        return retrofit.create(FcmService::class.java)
//    }



    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideFcmRepository(
        fcmService: FcmService,
        @ApplicationContext context: Context
    ): FcmRepository {
        return FcmRepository(
            fcmService = fcmService,
            context = context
        )
    }

}