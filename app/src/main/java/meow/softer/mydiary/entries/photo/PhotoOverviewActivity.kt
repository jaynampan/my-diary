package meow.softer.mydiary.entries.photo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import meow.softer.mydiary.R
import meow.softer.mydiary.shared.FileManager
import java.io.File

class PhotoOverviewActivity : AppCompatActivity() {
    /**
     * GUI
     */
    var RVDiaryPhotoOverview: RecyclerView? = null

    var RLDiaryPhotoOverviewNoImages: RelativeLayout? = null

    /**
     * The topic info
     */
    private var diaryPhotoFileList: ArrayList<Uri?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_photo_overview)

        RVDiaryPhotoOverview = this.findViewById<RecyclerView>(R.id.RV_diary_photo_overview)
        RLDiaryPhotoOverviewNoImages =
            this.findViewById<RelativeLayout>(R.id.RL_diary_photo_overview_no_images)
        //get topic id
        val topicId = intent.getLongExtra(PHOTO_OVERVIEW_TOPIC_ID, -1)
        //get topic fail , close this activity
        if (topicId == -1L) {
            Toast.makeText(
                this, getString(R.string.photo_viewer_topic_fail),
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
        val diaryId = intent.getLongExtra(PHOTO_OVERVIEW_DIARY_ID, -1)
        //Load the data
        loadDiaryImageData(topicId, diaryId)
        //Check any image is exist
        if (diaryPhotoFileList!!.size > 0) {
            initRecyclerView()
        } else {
            RLDiaryPhotoOverviewNoImages!!.visibility = View.VISIBLE
        }
    }

    private fun loadDiaryImageData(topicId: Long, diaryId: Long) {
        val diaryRoot = FileManager(this@PhotoOverviewActivity, FileManager.DIARY_ROOT_DIR)
        val topicRootFile: File?
        topicRootFile = if (diaryId != -1L) {
            File(diaryRoot.dirAbsolutePath + "/" + topicId + "/" + diaryId)
        } else {
            File(diaryRoot.dirAbsolutePath + "/" + topicId)
        }
        //Load all file form topic dir
        diaryPhotoFileList = ArrayList<Uri?>()
        for (photoFile in getFilesList(topicRootFile)) {
            diaryPhotoFileList!!.add(Uri.fromFile(photoFile))
        }
    }

    private fun getFilesList(parentDir: File): MutableList<File?> {
        val inFiles = ArrayList<File?>()
        val files = parentDir.listFiles()
        for (file in files!!) {
            if (file.isDirectory()) {
                inFiles.addAll(getFilesList(file))
            } else {
                inFiles.add(file)
            }
        }
        return inFiles
    }

    private fun initRecyclerView() {
        val layoutManager = GridLayoutManager(this, 3)
        RVDiaryPhotoOverview!!.setLayoutManager(layoutManager)
        val photoOverviewAdapter =
            PhotoOverviewAdapter(this@PhotoOverviewActivity, diaryPhotoFileList!!)
        RVDiaryPhotoOverview!!.setAdapter(photoOverviewAdapter)
        photoOverviewAdapter.setOnItemClickListener(object :
            PhotoOverviewAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val gotoPhotoDetailViewer =
                    Intent(this@PhotoOverviewActivity, PhotoDetailViewerActivity::class.java)
                gotoPhotoDetailViewer.putParcelableArrayListExtra(
                    PhotoDetailViewerActivity.DIARY_PHOTO_FILE_LIST, diaryPhotoFileList
                )
                gotoPhotoDetailViewer.putExtra(PhotoDetailViewerActivity.SELECT_POSITION, position)
                this@PhotoOverviewActivity.startActivity(gotoPhotoDetailViewer)
            }
        })
        RVDiaryPhotoOverview!!.setHasFixedSize(false)
    }

    companion object {
        const val PHOTO_OVERVIEW_TOPIC_ID: String = "PHOTOOVERVIEW_TOPIC_ID"
        const val PHOTO_OVERVIEW_DIARY_ID: String = "PHOTOOVERVIEW_DIARY_ID"
    }
}
