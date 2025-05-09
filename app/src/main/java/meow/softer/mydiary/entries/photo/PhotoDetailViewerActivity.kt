package meow.softer.mydiary.entries.photo

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import meow.softer.mydiary.R
import meow.softer.mydiary.shared.ScreenHelper

class PhotoDetailViewerActivity : AppCompatActivity() {
    /**
     * GUI
     */
    var VPDiaryPhotoDetail: ViewPager? = null


    private var mAdapter: PhotoDetailPagerAdapter? = null
    private var diaryPhotoFileList: ArrayList<Uri?>? = null
    private var selectPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Modify this activity into full screen mode
        ScreenHelper.closeImmersiveMode(window.decorView)
        setStatusBarColor()
        //Set the layout
        setContentView(R.layout.activity_diary_photo_detail_viewer)
        //ButterKnife.bind(this);
        VPDiaryPhotoDetail = this.findViewById<ViewPager>(R.id.VP_diary_photo_detail)

        //Modify the status bar color
        diaryPhotoFileList = intent.getParcelableArrayListExtra<Uri?>(DIARY_PHOTO_FILE_LIST)
        selectPosition = intent.getIntExtra(SELECT_POSITION, -1)
        if (diaryPhotoFileList == null || selectPosition == -1) {
            Toast.makeText(
                this,
                getString(R.string.photo_viewer_photo_path_fail),
                Toast.LENGTH_LONG
            ).show()
            finish()
        } else {
            //Init The view pager
            mAdapter = PhotoDetailPagerAdapter(supportFragmentManager, diaryPhotoFileList!!)
            VPDiaryPhotoDetail!!.setAdapter(mAdapter)
            VPDiaryPhotoDetail!!.setCurrentItem(selectPosition)
        }
    }

    private fun setStatusBarColor() {
        val window = getWindow()
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = Color.BLACK
    }

    companion object {
        const val DIARY_PHOTO_FILE_LIST: String = "DIARY_PHOTO_FILE_LIST"
        const val SELECT_POSITION: String = "SELECT_POSITION"
    }
}
