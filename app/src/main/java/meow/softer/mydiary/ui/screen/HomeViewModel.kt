package meow.softer.mydiary.ui.screen

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import meow.softer.mydiary.data.local.db.entity.TopicEntry
import meow.softer.mydiary.data.repository.FilesRepo
import meow.softer.mydiary.data.repository.SettingsRepo
import meow.softer.mydiary.data.repository.TopicRepo
import meow.softer.mydiary.ui.models.ITopic
import meow.softer.mydiary.util.debug
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo,
    private val topicRepo: TopicRepo,
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
    val isCroppingState = MutableStateFlow(false)
    val croppingBitmap = MutableStateFlow<Bitmap?>(null)

    private var saveOrderJob: Job? = null

    init {
        refresh()
    }

    private suspend fun loadData() {
        topicData.value = topicRepo.getAll()
    }

    private fun refresh() {
        viewModelScope.launch {
            loadSettings()
            loadData()
            loadUserProfile()
        }
    }

    private suspend fun loadUserProfile() {
        debug(TAG, "loading user profile...")
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
            userPicBitmap?.let {
                userPainter.value = BitmapPainter(it.asImageBitmap())
                croppingBitmap.value = it
                isCroppingState.value = true
            }
        }
    }

    fun updateCroppedUserProfile(bitmap: Bitmap) {
        viewModelScope.launch {
            userPainter.value = BitmapPainter(bitmap.asImageBitmap())
            filesRepo.saveUserPicBitmap(bitmap)
        }
    }

    fun closeCropping() {
        isCroppingState.value = false
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

    fun addITopic(name: String, type: Int, color: Color) {
        debug(TAG, "addITopic: $name, $type, $color")
        viewModelScope.launch {
            topicRepo.addTopic(TopicEntry(
                id = 0  ,
                title = name,
                subtitle = "",
                type = type,
                color = color.toArgb()
            ))
            loadData()
        }
    }

    fun moveTopic(fromIndex: Int, toIndex: Int) {
        val list = topicData.value.toMutableList()
        if (fromIndex < 0 || fromIndex >= list.size || toIndex < 0 || toIndex >= list.size) return
        val item = list.removeAt(fromIndex)
        list.add(toIndex, item)
        topicData.value = list
        
        // Debounce database updates
        saveOrderJob?.cancel()
        saveOrderJob = viewModelScope.launch {
            delay(1000) 
            topicRepo.updateTopicOrders(list)
        }
    }

    fun saveTopicOrder() {
        saveOrderJob?.cancel()
        val list = topicData.value
        viewModelScope.launch {
            withContext(NonCancellable) {
                topicRepo.updateTopicOrders(list)
            }
        }
    }

    companion object {
        const val TAG = "HomeViewModel"
    }
}