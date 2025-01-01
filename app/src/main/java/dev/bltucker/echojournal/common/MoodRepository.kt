package dev.bltucker.echojournal.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class MoodRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private val defaultMoodKey = stringPreferencesKey("default_mood")

    val defaultMood: Flow<Mood?> = context.dataStore.data.map { preferences ->
        preferences[defaultMoodKey]?.let { Mood.fromString(it) }
    }

    suspend fun setDefaultMood(mood: Mood) {
        context.dataStore.edit { preferences ->
            preferences[defaultMoodKey] = mood.name
        }
    }
}