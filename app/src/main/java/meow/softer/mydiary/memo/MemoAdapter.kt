package meow.softer.mydiary.memo

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.MotionEventCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.marshalchen.ultimaterecyclerview.itemTouchHelper.ItemTouchHelperViewHolder
import meow.softer.mydiary.R
import meow.softer.mydiary.db.DBManager
import meow.softer.mydiary.memo.EditMemoDialogFragment.Companion.newInstance
import meow.softer.mydiary.memo.EditMemoDialogFragment.MemoCallback
import meow.softer.mydiary.shared.EditMode
import meow.softer.mydiary.shared.ScreenHelper
import meow.softer.mydiary.shared.ThemeManager
import java.util.Collections

class MemoAdapter(
    private val mActivity: FragmentActivity, private val topicId: Long, //Data
    private val memoList: MutableList<MemoEntity>,
    private val dbManager: DBManager, private val callback: MemoCallback?,
    private val dragStartListener: OnStartDragListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>(), EditMode, ItemTouchHelperAdapter {
    private var isEditMode = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_memo_item, parent, false)
        return MemoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return memoList.size
    }

    override fun getItemId(position: Int): Long {
        return memoList[position].memoId
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MemoViewHolder) {
            holder.setItemPosition(position)
            holder.initView()
            setMemoContent(holder, position)
        }
    }

    private fun setMemoContent(holder: MemoViewHolder, position: Int) {
        if (memoList[position].isChecked) {
            val spannableContent = SpannableString(memoList[position].content)
            spannableContent.setSpan(
                StrikethroughSpan(),
                0,
                spannableContent.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            holder.tVContent.text = spannableContent
            holder.tVContent.setAlpha(0.4f)
        } else {
            holder.tVContent.text = memoList[position].content
            holder.tVContent.setAlpha(1f)
        }
    }

    override fun isEditMode(): Boolean {
        return isEditMode
    }

    override fun setEditMode(editMode: Boolean) {
        isEditMode = editMode
    }

    override fun onItemSwap(position: Int) {
        //Do nothing
    }

    override fun onItemMoveFinish() {
        //save the new order
        var orderNumber = memoList.size
        dbManager.openDB()
        dbManager.deleteAllCurrentMemoOrder(topicId)
        for (memoEntity in memoList) {
            dbManager.insertMemoOrder(topicId, memoEntity.memoId, (--orderNumber).toLong())
        }
        dbManager.closeDB()
        notifyDataSetChanged()
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(memoList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    private inner class MemoViewHolder(private val rootView: View) : RecyclerView.ViewHolder(
        rootView
    ), View.OnClickListener, OnTouchListener, ItemTouchHelperViewHolder {
        private val IV_memo_item_dot: ImageView
        val tVContent: TextView
        private val IV_memo_item_delete: ImageView
        private val RL_memo_item_root_view: RelativeLayout
        private var itemPosition = 0


        init {
            RL_memo_item_root_view =
                rootView.findViewById<RelativeLayout>(R.id.RL_memo_item_root_view)
            IV_memo_item_dot = rootView.findViewById<ImageView>(R.id.IV_memo_item_dot)
            this.tVContent = rootView.findViewById<TextView>(R.id.TV_memo_item_content)
            IV_memo_item_delete = rootView.findViewById<ImageView>(R.id.IV_memo_item_delete)
            tVContent.setTextColor(ThemeManager.getInstance().getThemeDarkColor(mActivity))
        }


        fun setItemPosition(itemPosition: Int) {
            this.itemPosition = itemPosition
        }

        fun initView() {
            if (isEditMode) {
                IV_memo_item_dot.setImageResource(R.drawable.ic_memo_swap_vert_black_24dp)
                val layoutParams = IV_memo_item_dot.layoutParams
                layoutParams.height = ScreenHelper.dpToPixel(mActivity.resources, 24)
                layoutParams.width = layoutParams.height
                IV_memo_item_delete.setVisibility(View.VISIBLE)
                IV_memo_item_dot.setOnTouchListener(this)
                IV_memo_item_delete.setOnClickListener(this)
                RL_memo_item_root_view.setOnClickListener(this)
            } else {
                IV_memo_item_dot.setImageResource(R.drawable.ic_memo_dot_24dp)
                val layoutParams = IV_memo_item_dot.layoutParams
                layoutParams.height = ScreenHelper.dpToPixel(mActivity.resources, 10)
                layoutParams.width = layoutParams.height
                IV_memo_item_delete.setVisibility(View.GONE)
                IV_memo_item_dot.setOnTouchListener(null)
                IV_memo_item_delete.setOnClickListener(null)
                RL_memo_item_root_view.setOnClickListener(this)
            }
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.IV_memo_item_delete -> {
                    dbManager.openDB()
                    dbManager.delMemo(memoList[itemPosition].memoId)
                    dbManager.deleteMemoOrder(memoList[itemPosition].memoId)
                    dbManager.closeDB()
                    memoList.removeAt(itemPosition)
                    notifyDataSetChanged()
                }

                R.id.RL_memo_item_root_view -> if (isEditMode) {
                    val editMemoDialogFragment = newInstance(
                        topicId,
                        memoList[itemPosition].memoId,
                        false,
                        memoList[itemPosition].content
                    )
                    editMemoDialogFragment.show(
                        mActivity.supportFragmentManager,
                        "editMemoDialogFragment"
                    )
                } else {
                    memoList[itemPosition].toggleChecked()
                    dbManager.openDB()
                    dbManager.updateMemoChecked(
                        memoList[itemPosition].memoId,
                        memoList[itemPosition].isChecked
                    )
                    dbManager.closeDB()
                    setMemoContent(this, itemPosition)
                }
            }
        }

        override fun onItemSelected() {
            RL_memo_item_root_view.setBackgroundColor(
                ThemeManager.getInstance().getThemeMainColor(mActivity)
            )
        }

        override fun onItemClear() {
            RL_memo_item_root_view.setBackgroundColor(Color.WHITE)
        }

        override fun onTouch(v: View?, event: MotionEvent): Boolean {
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                dragStartListener.onStartDrag(this)
            }
            return false
        }
    }
}
