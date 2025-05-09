package meow.softer.mydiary.shared.gui

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.common.RotationOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import meow.softer.mydiary.R
import meow.softer.mydiary.entries.diary.item.DiaryItemHelper.Companion.getVisibleHeight
import meow.softer.mydiary.entries.diary.item.DiaryItemHelper.Companion.getVisibleWidth
import meow.softer.mydiary.shared.ScreenHelper

class DiaryPhotoLayout : LinearLayout {
    private var SDV_diary_new_photo: SimpleDraweeView? = null
    private var IV_diary_photo_delete: ImageView? = null

    constructor(context: Context?) : super(context)
    constructor(activity: Activity) : super(activity) {
        val v = LayoutInflater.from(activity).inflate(R.layout.layout_diaryphoto, this, true)
        SDV_diary_new_photo = v.findViewById<SimpleDraweeView>(R.id.SDV_diary_new_photo)
        val params =
            SDV_diary_new_photo!!.layoutParams as RelativeLayout.LayoutParams
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT
        params.height = getVisibleHeight(activity)
        SDV_diary_new_photo!!.setLayoutParams(params)
        SDV_diary_new_photo!!.setAspectRatio(ScreenHelper.getScreenRatio(activity))
        IV_diary_photo_delete = v.findViewById<ImageView>(R.id.IV_diary_photo_delete)
    }

    fun setPhotoUri(photoUri: Uri?) {
        val request = ImageRequestBuilder.newBuilderWithSource(photoUri)
            .setResizeOptions(
                ResizeOptions(
                    getVisibleWidth(context),
                    getVisibleHeight(context)
                )
            )
            .setRotationOptions(RotationOptions.autoRotate())
            .build()
        val controller: DraweeController? = Fresco.newDraweeControllerBuilder()
            .setImageRequest(request)
            .build()
        SDV_diary_new_photo!!.setController(controller)
    }

    fun setDeleteOnClick(listener: OnClickListener?) {
        IV_diary_photo_delete!!.setOnClickListener(listener)
    }

    fun setDeletePositionTag(position: Int) {
        IV_diary_photo_delete!!.tag = position
    }

    fun setDraweeViewClick(listener: OnClickListener?) {
        SDV_diary_new_photo!!.setOnClickListener(listener)
    }

    fun setDraweeViewPositionTag(position: Int) {
        SDV_diary_new_photo!!.tag = position
    }

    fun setVisibleViewByMode(isEditMode: Boolean) {
        if (isEditMode) {
            IV_diary_photo_delete!!.setVisibility(VISIBLE)
        } else {
            IV_diary_photo_delete!!.setVisibility(GONE)
        }
    }

    val photo: SimpleDraweeView
        get() = SDV_diary_new_photo!!
}
