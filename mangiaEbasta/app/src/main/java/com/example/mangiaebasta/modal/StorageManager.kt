package com.example.mangiaebasta.modal

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "app_prefs")

object StorageManager {
    private val SID_KEY = stringPreferencesKey("sid")
    private val UID_KEY = stringPreferencesKey("uid")
    private val LAST_PAGE_KEY = stringPreferencesKey("last_page")
    private val SELECTED_MENU_KEY = stringPreferencesKey("selected_menu")

    fun getSid(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences: Preferences ->
            preferences[SID_KEY]
        }
    }

    suspend fun setSid(context: Context, sid: String) {
        context.dataStore.edit { preferences: MutablePreferences ->
            preferences[SID_KEY] = sid
        }
    }

    fun getUid(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences: Preferences ->
            preferences[UID_KEY]
        }
    }

    suspend fun setUid(context: Context, uid: String) {
        context.dataStore.edit { preferences: MutablePreferences ->
            preferences[UID_KEY] = uid
        }
    }

    fun getLastPage(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences: Preferences ->
            preferences[LAST_PAGE_KEY]
        }
    }

    suspend fun setLastPage(context: Context, lastPage: String) {
        context.dataStore.edit { preferences: MutablePreferences ->
            preferences[LAST_PAGE_KEY] = lastPage
        }
    }


    suspend fun setSelectedMenu(context: Context, menu: Menu) {
        val menuJson = Gson().toJson(menu)
        context.dataStore.edit { preferences: MutablePreferences ->
            preferences[SELECTED_MENU_KEY] = menuJson
        }
    }

    fun getSelectedMenu(context: Context): Flow<Menu?> {
        return context.dataStore.data.map { preferences: Preferences ->
            preferences[SELECTED_MENU_KEY]?.let { Gson().fromJson(it, Menu::class.java) }
        }
    }

}