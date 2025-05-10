package meow.softer.mydiary.ui.home

import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import meow.softer.mydiary.R

class MainViewModel : ViewModel() {
    val userName = MutableStateFlow("User")
    val userPic = MutableStateFlow(R.drawable.ic_person_picture_default)
    val userPainter = MutableStateFlow<Painter?>(null)
    val headerBgPainter = MutableStateFlow<Painter?>(null)

    fun updateUserName(value: String) {
        userName.value = value
    }

    fun updateUserPic(value:Painter ) {
        userPainter.value = value
    }
    fun updateHeaderBgPic(value:Painter ) {
        headerBgPainter.value = value
    }
}