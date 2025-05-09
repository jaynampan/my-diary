package meow.softer.mydiary.entries.photo

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import meow.softer.mydiary.R
import meow.softer.mydiary.entries.photo.PhotoOverviewAdapter.SimpleViewHolder
import meow.softer.mydiary.shared.ScreenHelper

class PhotoOverviewAdapter(
    private val mContext: Context,
    private val diaryPhotoFileList: ArrayList<Uri?>
) : RecyclerView.Adapter<SimpleViewHolder?>() {
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }


    private val heightMap: MutableMap<Uri?, Int?> = HashMap<Uri?, Int?>()
    private val widthMap: MutableMap<Uri?, Int?> = HashMap<Uri?, Int?>()
    private var mItemClickListener: OnItemClickListener? = null
    private val resizePhotoWidth: Int = ScreenHelper.getScreenWidth(mContext) / 3
    private val resizePhotoHeight: Int = ScreenHelper.dpToPixel(mContext.resources, 150)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SimpleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_diary_photo_overview_item, parent, false)
        return SimpleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        val request = ImageRequestBuilder.newBuilderWithSource(diaryPhotoFileList[position])
            .setResizeOptions(ResizeOptions(resizePhotoWidth, resizePhotoHeight))
            .build()
        val controller: DraweeController? = Fresco.newDraweeControllerBuilder()
            .setImageRequest(request)
            .setTapToRetryEnabled(false)
            .build()
        holder.SDV_CV_diary_photo_overview.setController(controller)
    }

    override fun getItemCount(): Int {
        return diaryPhotoFileList.size
    }


    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        this.mItemClickListener = mItemClickListener
    }

    open inner class SimpleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val SDV_CV_diary_photo_overview: SimpleDraweeView = itemView.findViewById<View?>(R.id.SDV_CV_diary_photo_overview) as SimpleDraweeView
        val CV_diary_photo_overview: CardView? = itemView.findViewById<View?>(R.id.CV_diary_photo_overview) as CardView?

        init {
            this.itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (mItemClickListener != null) {
                mItemClickListener!!.onItemClick(getAdapterPosition())
            }
        }
    }
}
