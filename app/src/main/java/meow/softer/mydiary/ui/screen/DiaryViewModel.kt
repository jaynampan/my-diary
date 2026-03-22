package meow.softer.mydiary.ui.screen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import meow.softer.mydiary.data.local.db.entity.DiaryEntry
import meow.softer.mydiary.data.local.db.entity.DiaryItem
import meow.softer.mydiary.data.repository.DiaryRepo
import javax.inject.Inject

data class DiaryUiState(
    val topicId: Int = 0,
    val topicTitle: String = "",
    val diaries: List<DiaryEntry> = emptyList(),
    val currentDiary: DiaryEntry? = null,
    val currentDiaryItems: List<DiaryItem> = emptyList(),
    val selectedTab: Int = 0, // 0: Entries, 1: Calendar, 2: Diary
    val isLoading: Boolean = false
)

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val diaryRepo: DiaryRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiaryUiState())
    val uiState: StateFlow<DiaryUiState> = _uiState.asStateFlow()

    fun initTopic(topicId: Int, topicTitle: String) {
        _uiState.value = _uiState.value.copy(topicId = topicId, topicTitle = topicTitle)
        loadDiaries()
    }

    fun loadDiaries() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val diaries = diaryRepo.getAllDiariesByTopicId(_uiState.value.topicId)
            _uiState.value = _uiState.value.copy(diaries = diaries, isLoading = false)
        }
    }

    fun selectTab(tab: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
        if (tab == 2 && _uiState.value.currentDiary == null) {
            createNewDiary()
        }
    }

    fun createNewDiary() {
        val now = (System.currentTimeMillis() / 1000).toInt()
        val newDiary = DiaryEntry(
            id = 0,
            refTopicId = _uiState.value.topicId,
            time = now,
            title = "",
            mood = 0,
            weather = 0,
            attachment = null,
            location = null
        )
        _uiState.value = _uiState.value.copy(
            currentDiary = newDiary,
            currentDiaryItems = listOf(DiaryItem(type = 0, position = 0, content = "", refDiaryId = 0)),
            selectedTab = 2
        )
    }

    fun editDiary(diary: DiaryEntry) {
        viewModelScope.launch {
            val items = diaryRepo.getDiaryItems(diary.id)
            _uiState.value = _uiState.value.copy(
                currentDiary = diary,
                currentDiaryItems = items.ifEmpty { listOf(DiaryItem(type = 0, position = 0, content = "", refDiaryId = diary.id)) },
                selectedTab = 2
            )
        }
    }

    fun updateDiaryTitle(title: String) {
        _uiState.value.currentDiary?.let {
            _uiState.value = _uiState.value.copy(currentDiary = it.copy(title = title))
        }
    }

    fun updateDiaryMood(mood: Int) {
        _uiState.value.currentDiary?.let {
            _uiState.value = _uiState.value.copy(currentDiary = it.copy(mood = mood))
        }
    }

    fun updateDiaryWeather(weather: Int) {
        _uiState.value.currentDiary?.let {
            _uiState.value = _uiState.value.copy(currentDiary = it.copy(weather = weather))
        }
    }

    fun updateDiaryItem(index: Int, content: String) {
        val currentItems = _uiState.value.currentDiaryItems.toMutableList()
        if (index in currentItems.indices) {
            currentItems[index] = currentItems[index].copy(content = content)
            _uiState.value = _uiState.value.copy(currentDiaryItems = currentItems)
        }
    }

    fun addImageItem(uri: Uri) {
        val currentItems = _uiState.value.currentDiaryItems.toMutableList()
        // Simple implementation: add image at the end or after current focused? 
        // For now, let's just add at the end.
        currentItems.add(DiaryItem(type = 1, position = currentItems.size, content = uri.toString(), refDiaryId = _uiState.value.currentDiary?.id ?: 0))
        // Also add a new text field after image if the last one wasn't text
        currentItems.add(DiaryItem(type = 0, position = currentItems.size, content = "", refDiaryId = _uiState.value.currentDiary?.id ?: 0))
        _uiState.value = _uiState.value.copy(currentDiaryItems = currentItems)
    }

    fun saveDiary() {
        viewModelScope.launch {
            val diary = _uiState.value.currentDiary ?: return@launch
            val items = _uiState.value.currentDiaryItems.filter { it.type == 1 || it.content.isNotBlank() }
            diaryRepo.saveDiary(diary, items)
            loadDiaries()
            _uiState.value = _uiState.value.copy(currentDiary = null, currentDiaryItems = emptyList(), selectedTab = 0)
        }
    }

    fun discardDiary() {
        _uiState.value = _uiState.value.copy(currentDiary = null, currentDiaryItems = emptyList(), selectedTab = 0)
    }
}
