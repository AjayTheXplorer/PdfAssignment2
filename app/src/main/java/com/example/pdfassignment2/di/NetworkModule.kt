package com.example.pdfassignment2.di

import android.content.Context
import com.example.pdfassignment2.model.apiService.FcmService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com/")  // Required!
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideFcmService(retrofit: Retrofit): FcmService {
        return retrofit.create(FcmService::class.java)
    }

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }
}
