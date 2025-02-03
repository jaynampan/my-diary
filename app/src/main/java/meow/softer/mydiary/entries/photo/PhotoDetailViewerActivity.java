package meow.softer.mydiary.entries.photo;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import meow.softer.mydiary.R;
import meow.softer.mydiary.shared.ScreenHelper;

import java.util.ArrayList;

public class PhotoDetailViewerActivity extends AppCompatActivity {
    public final static String DIARY_PHOTO_FILE_LIST = "DIARY_PHOTO_FILE_LIST";
    public final static String SELECT_POSITION = "SELECT_POSITION";

    /**
     * GUI
     */
    ViewPager VPDiaryPhotoDetail;


    private PhotoDetailPagerAdapter mAdapter;
    private ArrayList<Uri> diaryPhotoFileList;
    private int selectPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Modify this activity into full screen mode
        ScreenHelper.closeImmersiveMode(getWindow().getDecorView());
        setStatusBarColor();
        //Set the layout
        setContentView(R.layout.activity_diary_photo_detail_viewer);
        //ButterKnife.bind(this);
        VPDiaryPhotoDetail=this.findViewById(R.id.VP_diary_photo_detail);

        //Modify the status bar color
        diaryPhotoFileList = getIntent().getParcelableArrayListExtra(DIARY_PHOTO_FILE_LIST);
        selectPosition = getIntent().getIntExtra(SELECT_POSITION, -1);
        if (diaryPhotoFileList == null || selectPosition == -1) {
            Toast.makeText(this, getString(R.string.photo_viewer_photo_path_fail), Toast.LENGTH_LONG).show();
            finish();
        } else {
            //Init The view pager
            mAdapter = new PhotoDetailPagerAdapter(getSupportFragmentManager(), diaryPhotoFileList);
            VPDiaryPhotoDetail.setAdapter(mAdapter);
            VPDiaryPhotoDetail.setCurrentItem(selectPosition);
        }
    }

    private void setStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.BLACK);
    }
}
