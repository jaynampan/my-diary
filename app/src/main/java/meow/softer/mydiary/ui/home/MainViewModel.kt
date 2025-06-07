package meow.softer.mydiary.ui.home

import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import meow.softer.mydiary.main.topic.ITopic

class MainViewModel : ViewModel() {
    val userName = MutableStateFlow("User")
    val userPainter = MutableStateFlow<Painter?>(null)
    val headerBgPainter = MutableStateFlow<Painter?>(null)
    val topicData = MutableStateFlow<List<ITopic>>(listOf())

    fun updateUserName(value: String) {
        userName.value = value
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
        //Todo
    }

    fun getBackUpPath(): String? {
        TODO("Not yet implemented")
    }

    fun getImportPath(): String? {
        TODO("Not yet implemented")
    }

    fun updateImportPath(path: String) {
        TODO("Not yet implemented")
    }

}