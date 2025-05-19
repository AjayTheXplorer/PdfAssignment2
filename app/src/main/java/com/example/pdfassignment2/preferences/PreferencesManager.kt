package com.example.pdfassignment2.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton



import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore


//@Singleton
//class PreferencesManager @Inject constructor(@ApplicationContext private val context: Context) {
//
//    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//
//    fun setNotificationEnabled(enabled: Boolean) {
//        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
//    }
//
//    fun isNotificationEnabled(): Boolean {
//        return prefs.getBoolean("notifications_enabled", true)
//    }
//}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")


@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.dataStore

    private val NOTIFICATION_KEY = booleanPreferencesKey("notifications_enabled")

    val isNotificationEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[NOTIFICATION_KEY] ?: true
        }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_KEY] = enabled
        }
    }
}

