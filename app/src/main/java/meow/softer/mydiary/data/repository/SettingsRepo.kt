package meow.softer.mydiary.data.repository

import kotlinx.coroutines.flow.first
import meow.softer.mydiary.data.local.store.AppSettings
import meow.softer.mydiary.data.local.store.SettingStore
import javax.inject.Inject

class SettingsRepo @Inject constructor(
    private val settingStore: SettingStore
) {
    val settingsFlow = settingStore.settingsFlow

    suspend fun getUserSettings(): AppSettings {
        return settingStore.settingsFlow.first()
    }

    suspend fun updateUsername(value: String) {
        settingStore.setUsername(value)
    }

    suspend fun updateSecurityEnabled(enabled: Boolean) {
        settingStore.setSecurityEnabled(enabled)
    }
}