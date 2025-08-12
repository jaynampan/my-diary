package meow.softer.mydiary.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemState
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemViewHolder
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange
import com.h6ah4i.android.widget.advrecyclerview.draggable.annotation.DraggableItemStateFlags
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder
import meow.softer.mydiary.MainActivity
import meow.softer.mydiary.R
import meow.softer.mydiary.contacts.ContactsActivity
import meow.softer.mydiary.data.db.DBManager
import meow.softer.mydiary.entries.DiaryActivity
import meow.softer.mydiary.main.TopicDetailDialogFragment.Companion.newInstance
import meow.softer.mydiary.main.topic.ITopic
import meow.softer.mydiary.memo.MemoActivity
import meow.softer.mydiary.shared.ThemeManager
import meow.softer.mydiary.shared.ViewTools
import java.util.Locale

class MainTopicAdapter(
    private val activity: MainActivity,
    private val topicList: MutableList<ITopic>,
    private val dbManager: DBManager
) : RecyclerView.Adapter<MainTopicAdapter.TopicViewHolder>(), Filterable,
    DraggableItemAdapter<MainTopicAdapter.TopicViewHolder?>,
    SwipeableItemAdapter<MainTopicAdapter.TopicViewHolder?> {
    private var filteredTopicList: MutableList<ITopic?> = ArrayList<ITopic?>()
    private var topicFilter: TopicFilter? = null


    init {

        topicFilter = TopicFilter(this, topicList)

        // MainTopicAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true)
    }

    fun notifyDataSetChanged(clear: Boolean) {
        if (clear) {
            filteredTopicList.clear()
            filteredTopicList.addAll(topicList)
        }
        super.notifyDataSetChanged()
    }


    fun getList(): MutableList<ITopic?> {
        return filteredTopicList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_topic_item, parent, false)
        return TopicViewHolder(view)
    }

    override fun getItemId(position: Int): Long {
        return filteredTopicList[position]!!.id
    }

    override fun getItemCount(): Int {
        return filteredTopicList.size
    }

    override fun onBindViewHolder(holder: TopicViewHolder, pos: Int) {
        val position: Int = holder.getAdapterPosition()
        holder.swipeableContainerView.background =
            ThemeManager.instance!!.getTopicItemSelectDrawable(activity)
        holder.LL_topic_left_setting?.setBackgroundColor(
            ThemeManager.instance!!.getThemeMainColor(activity)
        )
        holder.iconView?.setImageResource(filteredTopicList[position]!!.icon)
        holder.iconView?.setColorFilter(filteredTopicList[position]!!.color)
        holder.titleView?.text = filteredTopicList[position]!!.title
        holder.titleView?.setTextColor(filteredTopicList[position]!!.color)
        holder.tVCount?.text = filteredTopicList[position]!!.count.toString()
        holder.tVCount?.setTextColor(filteredTopicList[position]!!.color)
        holder.arrow?.setColorFilter(filteredTopicList[position]!!.color)

        // set swiping properties
        holder.maxRightSwipeAmount = 0.3f
        holder.maxLeftSwipeAmount = 0F
        holder.swipeItemHorizontalSlideAmount =
            if (filteredTopicList[position]!!.isPinned) 0.3f else 0f

        //Click event
        holder.rLTopic?.setOnClickListener { v ->
            gotoTopic(
                filteredTopicList[position]!!.type,
                position
            )
        }
        holder.IV_topic_left_setting_edit?.setOnClickListener { v ->
            val createTopicDialogFragment =
                newInstance(
                    true,
                    position,
                    filteredTopicList[position]!!.id,
                    filteredTopicList[position]!!.title,
                    filteredTopicList[position]!!.type,
                    filteredTopicList[position]!!.color
                )
            createTopicDialogFragment.show(
                activity.supportFragmentManager,
                "createTopicDialogFragment"
            )
        }

        holder.IV_topic_left_setting_delete?.setOnClickListener { v ->
            val topicDeleteDialogFragment =
                TopicDeleteDialogFragment.newInstance(
                    position,
                    filteredTopicList[position]!!.title
                )
            topicDeleteDialogFragment.show(
                activity.supportFragmentManager,
                "topicDeleteDialogFragment"
            )
        }
    }

    fun gotoTopic(type: Int, position: Int) {
        when (type) {
            ITopic.TYPE_CONTACTS -> {
                val goContactsPageIntent = Intent(activity, ContactsActivity::class.java)
                goContactsPageIntent.putExtra(
                    "topicId",
                    filteredTopicList[position]!!.id
                )
                goContactsPageIntent.putExtra(
                    "diaryTitle",
                    filteredTopicList[position]!!.title
                )
                activity.startActivity(goContactsPageIntent)
            }

            ITopic.TYPE_DIARY -> {
                val goEntriesPageIntent = Intent(activity, DiaryActivity::class.java)
                goEntriesPageIntent.putExtra("topicId", filteredTopicList[position]!!.id)
                goEntriesPageIntent.putExtra(
                    "diaryTitle",
                    filteredTopicList[position]!!.title
                )
                goEntriesPageIntent.putExtra("has_entries", true)
                activity.startActivity(goEntriesPageIntent)
            }

            ITopic.TYPE_MEMO -> {
                val goMemoPageIntent = Intent(activity, MemoActivity::class.java)
                goMemoPageIntent.putExtra("topicId", filteredTopicList[position]!!.id)
                goMemoPageIntent.putExtra(
                    "diaryTitle",
                    filteredTopicList[position]!!.title
                )
                activity.startActivity(goMemoPageIntent)
            }
        }
    }

    override fun getFilter(): Filter {
        return topicFilter!!
    }

    /*
     * Swipe
     */
    override fun onGetSwipeReactionType(
        holder: TopicViewHolder,
        position: Int,
        x: Int,
        y: Int
    ): Int {
        return if (ViewTools.hitTest(holder.swipeableContainerView, x, y)) {
            SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H
        } else {
            SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_BOTH_H
        }
    }

    override fun onSwipeItemStarted(holder: TopicViewHolder, position: Int) {
    }

    override fun onSetSwipeBackground(holder: TopicViewHolder, position: Int, type: Int) {
        if (type == SwipeableItemConstants.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND) {
            holder.topicLeftSettingView?.visibility = View.GONE
        } else {
            holder.topicLeftSettingView?.visibility = View.VISIBLE
        }
    }


    override fun onSwipeItem(
        holder: TopicViewHolder,
        position: Int,
        result: Int
    ): SwipeResultAction? {
        when (result) {
            SwipeableItemConstants.RESULT_SWIPED_RIGHT -> return SwipeRightResultAction(
                this,
                position
            )

            SwipeableItemConstants.RESULT_SWIPED_LEFT, SwipeableItemConstants.RESULT_CANCELED -> return if (position != RecyclerView.NO_POSITION) {
                UnpinResultAction(this, position)
            } else {
                null
            }

            else -> return if (position != RecyclerView.NO_POSITION) {
                UnpinResultAction(this, position)
            } else {
                null
            }
        }
    }

    override fun onCheckCanStartDrag(
        holder: TopicViewHolder,
        position: Int,
        x: Int,
        y: Int
    ): Boolean {
        // x, y --- relative from the itemView's top-left

        val containerView = holder.swipeableContainerView

        val offsetX =
            containerView.left + (ViewCompat.getTranslationX(containerView) + 0.5f).toInt()
        val offsetY =
            containerView.top + (ViewCompat.getTranslationY(containerView) + 0.5f).toInt()

        return !topicFilter!!.isFilter && ViewTools.hitTest(containerView, x - offsetX, y - offsetY)
    }


    override fun onGetItemDraggableRange(
        holder: TopicViewHolder,
        position: Int
    ): ItemDraggableRange? {
        return null
    }

    override fun onMoveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) {
            return
        }

        //modify the original list
        val originalItem = topicList.removeAt(fromPosition)
        topicList.add(toPosition, originalItem)

        //Modify the filter list
        val filteredItem = filteredTopicList.removeAt(fromPosition)
        filteredTopicList.add(toPosition, filteredItem)

        //save the new topic order
        var orderNumber = topicList.size
        dbManager.openDB()
        dbManager.deleteAllCurrentTopicOrder()
        for (topic in topicList) {
            dbManager.insertTopicOrder(topic.id, (--orderNumber).toLong())
        }
        dbManager.closeDB()
        notifyDataSetChanged(false)
    }

    override fun onCheckCanDrop(draggingPosition: Int, dropPosition: Int): Boolean {
        return true
    }

    override fun onItemDragStarted(position: Int) {
    }

    override fun onItemDragFinished(fromPosition: Int, toPosition: Int, result: Boolean) {
    }

    private class SwipeRightResultAction(adapter: MainTopicAdapter?, position: Int) :
        SwipeResultActionMoveToSwipedDirection() {
        private var mAdapter: MainTopicAdapter?
        private val mPosition: Int

        init {
            mAdapter = adapter
            mPosition = position
        }

        override fun onPerformAction() {
            super.onPerformAction()

            val item: ITopic = mAdapter?.getList()[mPosition]!!

            if (!item.isPinned) {
                item.isPinned = true
                mAdapter?.notifyItemChanged(mPosition)
            }
        }

        override fun onCleanUp() {
            super.onCleanUp()
            // clear the references
            mAdapter = null
        }
    }

    private class UnpinResultAction(
        private var mAdapter: MainTopicAdapter?,
        private val mPosition: Int
    ) :
        SwipeResultActionDefault() {


        override fun onPerformAction() {
            super.onPerformAction()

            val item: ITopic = mAdapter?.getList()[mPosition]!!
            if (item.isPinned) {
                item.isPinned = false
                mAdapter?.notifyItemChanged(mPosition)
            }
        }

        override fun onSlideAnimationEnd() {
            super.onSlideAnimationEnd()
        }

        override fun onCleanUp() {
            super.onCleanUp()
            // clear the references
            mAdapter = null
        }
    }

    class TopicViewHolder(rootView: View) :
        AbstractSwipeableItemViewHolder(rootView), DraggableItemViewHolder {
        @DraggableItemStateFlags
        private var mDragStateFlags = 0

        val iconView: ImageView? = rootView.findViewById<ImageView?>(R.id.IV_topic_icon)
        val titleView: TextView? = rootView.findViewById<TextView?>(R.id.TV_topic_title)
        val tVCount: TextView? = rootView.findViewById<TextView?>(R.id.TV_topic_count)
        val arrow: ImageView? = rootView.findViewById<ImageView?>(R.id.IV_topic_arrow_right)

        //Left setting view
        val rLTopic: RelativeLayout? = rootView.findViewById<RelativeLayout?>(R.id.RL_topic_view)
        val LL_topic_left_setting: LinearLayout? = rootView.findViewById<LinearLayout?>(R.id.LL_topic_left_setting)
        val RL_topic_content: RelativeLayout =
            rootView.findViewById<RelativeLayout?>(R.id.RL_topic_content)
        val IV_topic_left_setting_edit: ImageView? = rootView.findViewById<ImageView?>(R.id.IV_topic_left_setting_edit)
        val IV_topic_left_setting_delete: ImageView? = rootView.findViewById<ImageView?>(R.id.IV_topic_left_setting_delete)

        override fun getSwipeableContainerView(): View {
            return RL_topic_content
        }

        override fun setDragStateFlags(@DraggableItemStateFlags flags: Int) {
            mDragStateFlags = flags
        }

        @DraggableItemStateFlags
        override fun getDragStateFlags(): Int {
            return mDragStateFlags
        }

        override fun getDragState(): DraggableItemState {
            return DraggableItemState() //todo: fix this
        }


        val topicLeftSettingView: View?
            get() = LL_topic_left_setting

    }

    private class TopicFilter(
        private val adapter: MainTopicAdapter,
        private val originalList: MutableList<ITopic>
    ) :
        Filter() {

        private val filteredList: MutableList<ITopic?> = ArrayList<ITopic?>()

        var isFilter: Boolean = false

        override fun performFiltering(constraint: CharSequence): FilterResults {
            filteredList.clear()
            val results = FilterResults()

            if (constraint.isEmpty()) {
                filteredList.addAll(originalList)
                isFilter = false
            } else {
                val filterPattern =
                    constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (topic in originalList) {
                    if (topic.title?.lowercase(Locale.getDefault())!!.contains(filterPattern)) {
                        filteredList.add(topic)
                    }
                }
                isFilter = true
            }
            results.values = filteredList
            results.count = filteredList.size
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            adapter.filteredTopicList.clear()
            adapter.filteredTopicList.addAll(results.values as ArrayList<ITopic?>)
            adapter.notifyDataSetChanged(false)
        }
    }
}