package meow.softer.mydiary.entries

import androidx.fragment.app.Fragment
import meow.softer.mydiary.entries.entry.EntriesEntity


open class BaseDiaryFragment : Fragment() {
    val topicId: Long
        get() = (activity as DiaryActivity).topicId
    val entriesList: MutableList<EntriesEntity?>?
        get() = (activity as DiaryActivity).entriesList

    fun updateEntriesList() {
        (activity as DiaryActivity).loadEntries()
    }
}
