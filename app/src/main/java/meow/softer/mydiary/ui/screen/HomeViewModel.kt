package meow.softer.mydiary.ui.screen

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import meow.softer.mydiary.data.repository.SettingsRepo
import meow.softer.mydiary.main.topic.ITopic
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo
) : ViewModel() {
    val userName = MutableStateFlow("User")
    val userPainter = MutableStateFlow<Painter?>(null)
    val headerBgPainter = MutableStateFlow<Painter?>(null)
    val topicData = MutableStateFlow<List<ITopic>>(listOf())
    val contactTitle = MutableStateFlow("")
    val importPath = MutableStateFlow("")
    val exportPath = MutableStateFlow("")
    val contacts = MutableStateFlow<List<ContactGroup>>(emptyList())
    val contactBackgroundPainter = MutableStateFlow<Painter?>(null)


    init {
        refresh()
    }

    private  fun refresh() {
        viewModelScope.launch {
            val appSettings = settingsRepo.getUserSettings()
            userName.value = appSettings.username
        }
    }

    fun updateUserName(value: String) {
        userName.value = value
        viewModelScope.launch {
            settingsRepo.updateUsername(value)
        }
    }

    fun updateUserPic(value: Painter) {
        userPainter.value = value
    }

    fun updateHeaderBgPic(value: Painter) {
        headerBgPainter.value = value
    }

    fun updateTopicData(value: List<ITopic>) {
        topicData.value = value
    }

    fun updateBackUpSrc(path: String) {
        exportPath.value = path
    }

    fun updateImportPath(path: String) {
        importPath.value = path
    }

    fun updateContactTitle(title: String) {
        contactTitle.value = title

    }

    fun updateContactBackground(painter: BitmapPainter) {
        contactBackgroundPainter.value = painter
    }
}