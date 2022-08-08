package com.example.application_story.model.api

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

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreference  @Inject constructor(@ApplicationContext val context: Context){

    private val dataStore = context.dataStore

    fun getUser() : Flow<LoginResult> {
        return dataStore.data.map { preference ->
            LoginResult(
               preference[KEY_NAME] ?:"",
                preference[KEY_USERID] ?:"",
                preference[KEY_TOKEN] ?:""
            )
        }
    }

    suspend fun saveUser(user: LoginResult) {
            dataStore.edit { preference ->
                preference[KEY_NAME] = user.name
                preference[KEY_USERID] = user.userId
                preference[KEY_TOKEN] = user.token
            }

    }

    suspend fun logout() {
        dataStore.edit {
            it.clear()
        }
    }

    companion object {
        private val KEY_NAME = stringPreferencesKey("name")
        private val KEY_USERID = stringPreferencesKey("userId")
        private val KEY_TOKEN = stringPreferencesKey("token")
    }
}