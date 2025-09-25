package meow.softer.mydiary.data.repository

import kotlinx.coroutines.flow.first
import meow.softer.mydiary.data.local.store.AppSettings
import meow.softer.mydiary.data.local.store.SettingStore
import javax.inject.Inject

class SettingsRepo @Inject constructor(
    private val settingStore: SettingStore
) {
    suspend fun getUserSettings(): AppSettings {
        return settingStore.settingsFlow.first()
    }

    suspend fun updateUsername(value: String) {
        settingStore.setUsername(value)
    }

}