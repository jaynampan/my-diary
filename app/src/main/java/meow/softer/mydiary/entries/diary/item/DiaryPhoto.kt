package meow.softer.mydiary.entries.diary.item

import android.app.Activity
import android.net.Uri
import android.view.View
import meow.softer.mydiary.entries.diary.item.IDiaryRow.Companion.TYPE_PHOTO
import meow.softer.mydiary.shared.gui.DiaryPhotoLayout

class DiaryPhoto(
    activity: Activity?,
    override var content: String? = null ,
    override val type: Int = TYPE_PHOTO,
    override val view: View? = null
) : IDiaryRow {
    private val diaryPhotoLayout: DiaryPhotoLayout = DiaryPhotoLayout(activity)
    private var photoFileName: String? = null
    private var photoUri: Uri? = null
    override var position = 0

    init {
        //Default is editable
        setEditMode(true)
    }

    /**
     * This tag is only used in view mode
     * @param draweeViewPositionTag
     */
    fun setDraweeViewPositionTag(draweeViewPositionTag: Int) {
        diaryPhotoLayout.setDraweeViewPositionTag(draweeViewPositionTag)
    }

    /**
     * Edit mode , the delete button
     *
     * @param clickListener
     */
    fun setDeleteClickListener(clickListener: View.OnClickListener?) {
        diaryPhotoLayout.setDeleteOnClick(clickListener)
    }

    /**
     * The view mode , you can click to open the large imgae
     *
     * @param clickListener
     */
    fun setDraweeViewClickListener(clickListener: View.OnClickListener?) {
        diaryPhotoLayout.setDraweeViewClick(clickListener)
    }


    fun setPhoto(photoUri: Uri?, photoFileName: String?) {
        this.photoUri = photoUri
        this.photoFileName = photoFileName
        diaryPhotoLayout.setPhotoUri(photoUri)
    }

    fun setPhotoFileName(photoFileName: String?) {
        this.photoFileName = photoFileName
    }

    fun setDeletePosition(position: Int) {
        //When  content is modified(e.g.insert or delete) , update setDeletePositionTag
        diaryPhotoLayout.setDeletePositionTag(position)
    }

    override fun setEditMode(isEditMode: Boolean) {
        diaryPhotoLayout.setVisibleViewByMode(isEditMode)
    }
}
