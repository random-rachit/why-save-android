package com.rachitbhutani.whysave.helper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class WhySaveDataStore @Inject constructor(@ApplicationContext private val context: Context) {

    private val userOnboardedKey = booleanPreferencesKey("user_onboard")

    suspend fun updateOnboardedKey() = context.dataStore.edit {
        it[userOnboardedKey] = true
    }

    fun isUserOnboarded() = context.dataStore.data.map {
        it[userOnboardedKey] ?: false
    }
}
