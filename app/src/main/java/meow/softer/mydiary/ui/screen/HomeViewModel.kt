package meow.softer.mydiary.ui.screen

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import meow.softer.mydiary.data.local.db.DiaryDatabase
import meow.softer.mydiary.data.repository.FilesRepo
import meow.softer.mydiary.data.repository.SettingsRepo
import meow.softer.mydiary.ui.models.ITopic
import meow.softer.mydiary.ui.models.Memo
import meow.softer.mydiary.util.debug
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo,
    private val db: DiaryDatabase,
    private val filesRepo: FilesRepo
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

    private suspend fun loadData() {
        withContext(Dispatchers.IO) {
            val entries = db.topicDao().getAll()
            val topics = mutableListOf<ITopic>()
            debug("HomeViewModel", "size: ${entries.size}")
            entries.forEach {
                topics.add(Memo(id = it.id.toLong(), title = it.name, color = Color.Red.toArgb()))
            }
            topicData.value = topics
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            loadSettings()
            loadData()
            loadUserProfile()
        }
    }

    private suspend fun loadUserProfile() {
        debug(TAG,"loading user profile...")
        filesRepo.getUserPic()?.asImageBitmap()?.let {
            userPainter.value = BitmapPainter(it)
            debug(TAG, "user profile loaded!")
        }
    }

    private suspend fun loadSettings() {
        val appSettings = settingsRepo.getUserSettings()
        userName.value = appSettings.username
    }

    fun updateUserName(value: String) {
        userName.value = value
        viewModelScope.launch {
            settingsRepo.updateUsername(value)
        }
    }

    fun updateUserProfilePic(uri: Uri) {
        viewModelScope.launch {
            val userPicBitmap = filesRepo.saveUserPic(uri)
            userPicBitmap?.asImageBitmap()?.let {
                userPainter.value = BitmapPainter(it)
            }
        }
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

    companion object {
        const val TAG = "HomeViewModel"
    }
}