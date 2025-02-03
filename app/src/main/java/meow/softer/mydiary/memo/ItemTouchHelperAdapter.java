package meow.softer.mydiary.memo;

public interface ItemTouchHelperAdapter {
    void onItemMove(int fromPosition, int toPosition);

    void onItemSwap(int position);

    void onItemMoveFinish();
}
