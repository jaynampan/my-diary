package meow.softer.mydiary.backup.obj

class BUDiaryEntries(
    val diaryEntriesId: Long, val diaryEntriesTime: Long, val diaryEntriesTitle: String?,
    val diaryEntriesMood: Int, val diaryEntriesWeather: Int,
    val isDiaryEntriesAttachment: Boolean, val diaryEntriesLocation: String?,
    val diaryItemList: MutableList<BUDiaryItem?>?
) {
    companion object {
        val NO_BU_DIARY_ID: Long = -1
        val NO_BU_DIARY_TIME: Long = -1
    }
}
