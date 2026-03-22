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
            val maxPosition = _memoData.value.maxByOrNull { it.position }?.position ?: -1
            val newMemo = MemoEntry(
                content = content,
                refTopicId = currentTopicId,
                position = maxPosition + 1
            )
            memoRepo.addMemo(newMemo)
            loadMemos(currentTopicId)
        }
    }

    fun updateMemoContent(memo: MemoEntry, newContent: String) {
        viewModelScope.launch {
            val updatedMemo = memo.copy(content = newContent)
            memoRepo.updateMemo(updatedMemo)
            loadMemos(currentTopicId)
        }
    }

    fun deleteMemo(memo: MemoEntry) {
        viewModelScope.launch {
            memoRepo.deleteMemo(memo)
            loadMemos(currentTopicId)
        }
    }

    fun moveUp(memo: MemoEntry) {
        val currentIndex = _memoData.value.indexOf(memo)
        if (currentIndex > 0) {
            val prevMemo = _memoData.value[currentIndex - 1]
            swapPositions(memo, prevMemo)
        }
    }

    fun moveDown(memo: MemoEntry) {
        val currentIndex = _memoData.value.indexOf(memo)
        if (currentIndex < _memoData.value.size - 1) {
            val nextMemo = _memoData.value[currentIndex + 1]
            swapPositions(memo, nextMemo)
        }
    }

    private fun swapPositions(memo1: MemoEntry, memo2: MemoEntry) {
        viewModelScope.launch {
            val m1 = memo1.copy(position = memo2.position)
            val m2 = memo2.copy(position = memo1.position)
            // Ideally we should have an updateAll in Repo too, but for simplicity:
            memoRepo.updateMemo(m1)
            memoRepo.updateMemo(m2)
            loadMemos(currentTopicId)
        }
    }
}
