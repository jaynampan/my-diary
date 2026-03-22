package meow.softer.mydiary.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import meow.softer.mydiary.data.local.db.entity.MemoEntry
import meow.softer.mydiary.data.repository.MemoRepo
import javax.inject.Inject

@HiltViewModel
class MemoViewModel @Inject constructor(
    private val memoRepo: MemoRepo
) : ViewModel() {
    private val _memoData = MutableStateFlow<List<MemoEntry>>(listOf())
    val memoData: StateFlow<List<MemoEntry>> = _memoData

    private var currentTopicId: Int = -1

    fun loadMemos(topicId: Int) {
        currentTopicId = topicId
        viewModelScope.launch {
            _memoData.value = memoRepo.getAllByTopicId(topicId)
        }
    }

    fun toggleChecked(memo: MemoEntry) {
        viewModelScope.launch {
            val updatedMemo = memo.copy(checked = !memo.checked)
            memoRepo.updateMemo(updatedMemo)
            loadMemos(currentTopicId)
        }
    }

    fun addMemo(content: String) {
        if (currentTopicId == -1) return
        viewModelScope.launch {
            val newMemo = MemoEntry(content = content, refTopicId = currentTopicId)
            memoRepo.addMemo(newMemo)
            loadMemos(currentTopicId)
        }
    }

    fun deleteMemo(memo: MemoEntry) {
        viewModelScope.launch {
            memoRepo.deleteMemo(memo)
            loadMemos(currentTopicId)
        }
    }
}