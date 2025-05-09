package meow.softer.mydiary.memo

class MemoEntity(val memoId: Long, var content: String?, var isChecked: Boolean) {
    fun toggleChecked() {
        isChecked = !isChecked
    }
}
