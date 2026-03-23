package meow.softer.mydiary.data.local.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingStore(private val context: Context) {
    // data store keys
    private val themeKey = stringPreferencesKey("theme")
    private val languageKey = stringPreferencesKey("language")
    private val usernameKey = stringPreferencesKey("username")
    private val securityKey = booleanPreferencesKey("security")

    // read
    // Combine all settings into a single Flow
    val settingsFlow: Flow<AppSettings> = combine(
        context.datastore.data.map { it[themeKey]?.let(AppTheme::valueOf) ?: AppTheme.TAKI },
        context.datastore.data.map { it[languageKey]?.let(AppLanguage::valueOf) ?: AppLanguage.EN },
        context.datastore.data.map { it[usernameKey] ?: DEFAULT_USERNAME },
        context.datastore.data.map { it[securityKey] ?: false }
    ) { theme, language, username, security ->
        AppSettings(theme, language, username, security)
    }

    //write
    suspend fun setTheme(theme: AppTheme) {
        context.datastore.edit { it[themeKey] = theme.name }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.datastore.edit { it[languageKey] = language.name }
    }

    suspend fun setUsername(username: String) {
        context.datastore.edit { it[usernameKey] = username }
    }

    suspend fun setSecurityEnabled(enabled: Boolean) {
        context.datastore.edit { it[securityKey] = enabled }
    }
}