package meow.softer.mydiary.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import meow.softer.mydiary.data.local.store.AppSettings
import meow.softer.mydiary.data.repository.SettingsRepo
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepo.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings()
    )

    fun setSecurityEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.updateSecurityEnabled(enabled)
        }
    }
}