package meow.softer.mydiary.memo

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)

    fun onItemSwap(position: Int)

    fun onItemMoveFinish()
}
