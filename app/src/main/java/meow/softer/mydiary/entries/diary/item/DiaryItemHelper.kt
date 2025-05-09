package meow.softer.mydiary.entries.diary.item

import android.content.Context
import android.util.Log
import android.widget.LinearLayout
import meow.softer.mydiary.R
import meow.softer.mydiary.shared.ScreenHelper
import meow.softer.mydiary.shared.statusbar.ChinaPhoneHelper
import java.util.Observable

class DiaryItemHelper(private val itemContentLayout: LinearLayout) : Observable() {
    //For test to Public
    private val diaryItemList: MutableList<IDiaryRow?>
    var nowPhotoCount: Int = 0
        private set


    init {
        this.diaryItemList = ArrayList<IDiaryRow?>()
    }

    fun initDiary() {
        //Remove old data
        itemContentLayout.removeAllViews()
        diaryItemList.clear()
        nowPhotoCount = 0
        setChanged()
        notifyObservers()
    }

    fun createItem(diaryItem: IDiaryRow) {
        if (diaryItem is DiaryPhoto) {
            nowPhotoCount++
        }
        Log.e("Mytest", "DiaryItemHelper diaryItem:" + diaryItem.toString())
        diaryItemList.add(diaryItem)
        Log.e("Mytest", "DiaryItemHelper diaryItemL.list:" + diaryItemList.toString())
        Log.e("Mytest", "DiaryItemHelper itemContentLayout:" + itemContentLayout.toString())
        itemContentLayout.addView(diaryItemList[diaryItemList.size - 1]!!.view)
        if (diaryItemList.size == 1) {
            setChanged()
            notifyObservers()
        }
    }

    fun createItem(diaryItem: IDiaryRow, position: Int) {
        if (diaryItem is DiaryPhoto) {
            nowPhotoCount++
        }
        diaryItemList.add(position, diaryItem)
        itemContentLayout.addView(diaryItem.view, position)
        if (diaryItemList.size == 1) {
            setChanged()
            notifyObservers()
        }
    }

    val itemSize: Int
        get() = diaryItemList.size

    fun get(position: Int): IDiaryRow? {
        return diaryItemList[position]
    }

    fun remove(position: Int) {
        if (diaryItemList[position] is DiaryPhoto) {
            nowPhotoCount--
        }
        diaryItemList.removeAt(position)
        if (diaryItemList.isEmpty()) {
            setChanged()
            notifyObservers()
        }
    }

    fun resortPosition() {
        for (i in diaryItemList.indices) {
            diaryItemList[i]!!.position = i
        }
    }

    fun mergerAdjacentText(position: Int) {
        if (diaryItemList.isNotEmpty() && diaryItemList[position]!!
                .type == IDiaryRow.TYPE_TEXT
        ) {
            if (position != 0 && diaryItemList[position - 1]!!
                    .type == IDiaryRow.TYPE_TEXT
            ) {
                //First Item
                val mergerStr = diaryItemList[position]!!.content
                (diaryItemList[position - 1] as DiaryText).insertText(mergerStr)
                itemContentLayout.removeViewAt(position)
                diaryItemList.removeAt(position)
            }
        }
    }


    companion object {
        const val MAX_PHOTO_COUNT: Int = 7


        /**
         * Make all item < 1 screen, so It should be computed show area.
         * The height & Width is fixed value for a device.
         */
        @JvmStatic
        fun getVisibleHeight(context: Context): Int {
            val topbarHeight =
                context.resources.getDimensionPixelOffset(R.dimen.top_bar_height)
            val imageHeight: Int
            if (ChinaPhoneHelper.getDeviceStatusBarType() == ChinaPhoneHelper.OTHER) {
                imageHeight = (ScreenHelper.getScreenHeight(context)
                        - ScreenHelper.getStatusBarHeight(context) //diary activity top bar  -( diary info + diary bottom bar + diary padding+ photo padding)
                        - topbarHeight - ScreenHelper.dpToPixel(
                    context.resources,
                    120 + 40 + (2 * 5) + (2 * 5)
                ))
            } else {
                imageHeight =
                    (ScreenHelper.getScreenHeight(context) //diary activity top bar  -( diary info + diary bottom bar + diary padding + photo padding)
                            - topbarHeight - ScreenHelper.dpToPixel(
                        context.resources,
                        120 + 40 + (2 * 5) + (2 * 5)
                    ))
            }
            return imageHeight
        }

        @JvmStatic
        fun getVisibleWidth(context: Context): Int {
            val imageWeight =
                ScreenHelper.getScreenWidth(context) -  //(diary padding + photo padding)
                        ScreenHelper.dpToPixel(context.resources, (2 * 5) + (2 * 5))
            return imageWeight
        }
    }
}
