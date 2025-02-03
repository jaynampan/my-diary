package meow.softer.mydiary.entries.photo;

import static meow.softer.mydiary.shared.FileManager.DIARY_ROOT_DIR;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import meow.softer.mydiary.R;
import meow.softer.mydiary.shared.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class PhotoOverviewActivity extends AppCompatActivity {
    public final static String PHOTO_OVERVIEW_TOPIC_ID = "PHOTOOVERVIEW_TOPIC_ID";
    public final static String PHOTO_OVERVIEW_DIARY_ID = "PHOTOOVERVIEW_DIARY_ID";


    /**
     * GUI
     */

    RecyclerView RVDiaryPhotoOverview;

    RelativeLayout RLDiaryPhotoOverviewNoImages;

    /**
     * The topic info
     */
    private ArrayList<Uri> diaryPhotoFileList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_photo_overview);

        RVDiaryPhotoOverview = this.findViewById(R.id.RV_diary_photo_overview);
        RLDiaryPhotoOverviewNoImages = this.findViewById(R.id.RL_diary_photo_overview_no_images);
        //get topic id
        long topicId = getIntent().getLongExtra(PHOTO_OVERVIEW_TOPIC_ID, -1);
        //get topic fail , close this activity
        if (topicId == -1) {
            Toast.makeText(this, getString(R.string.photo_viewer_topic_fail)
                    , Toast.LENGTH_LONG).show();
            finish();
        }
        long diaryId = getIntent().getLongExtra(PHOTO_OVERVIEW_DIARY_ID, -1);
        //Load the data
        loadDiaryImageData(topicId, diaryId);
        //Check any image is exist
        if (diaryPhotoFileList.size() > 0) {
            initRecyclerView();
        } else {
            RLDiaryPhotoOverviewNoImages.setVisibility(View.VISIBLE);
        }
    }

    private void loadDiaryImageData(long topicId, long diaryId) {
        FileManager diaryRoot = new FileManager(PhotoOverviewActivity.this, DIARY_ROOT_DIR);
        File topicRootFile;
        if (diaryId != -1) {
            topicRootFile = new File(diaryRoot.getDirAbsolutePath() + "/" + topicId + "/" + diaryId);
        } else {
            topicRootFile = new File(diaryRoot.getDirAbsolutePath() + "/" + topicId);
        }
        //Load all file form topic dir
        diaryPhotoFileList = new ArrayList<>();
        for (File photoFile : getFilesList(topicRootFile)) {
            diaryPhotoFileList.add(Uri.fromFile(photoFile));
        }
    }

    private List<File> getFilesList(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getFilesList(file));
            } else {
                inFiles.add(file);
            }
        }
        return inFiles;
    }

    private void initRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        RVDiaryPhotoOverview.setLayoutManager(layoutManager);
        PhotoOverviewAdapter photoOverviewAdapter = new PhotoOverviewAdapter(PhotoOverviewActivity.this, diaryPhotoFileList);
        RVDiaryPhotoOverview.setAdapter(photoOverviewAdapter);
        photoOverviewAdapter.setOnItemClickListener(new PhotoOverviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent gotoPhotoDetailViewer = new Intent(PhotoOverviewActivity.this, PhotoDetailViewerActivity.class);
                gotoPhotoDetailViewer.putParcelableArrayListExtra(
                        PhotoDetailViewerActivity.DIARY_PHOTO_FILE_LIST, diaryPhotoFileList);
                gotoPhotoDetailViewer.putExtra(PhotoDetailViewerActivity.SELECT_POSITION, position);
                PhotoOverviewActivity.this.startActivity(gotoPhotoDetailViewer);
            }
        });
        RVDiaryPhotoOverview.setHasFixedSize(false);
    }
}
