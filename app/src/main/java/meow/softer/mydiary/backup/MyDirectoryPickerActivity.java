package meow.softer.mydiary.backup;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import meow.softer.mydiary.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyDirectoryPickerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MyFileItemAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private TextView tv_current_dir;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_filepicker);
        recyclerView = findViewById(R.id.RV_filepicker);
        tv_current_dir=findViewById(R.id.my_current_dir);

        checkAndRequestReadPermission();

        File rootDir = Environment.getExternalStorageDirectory();
        tv_current_dir.setText(rootDir.getPath());
        Log.e("Mytest","root dir:"+rootDir.getPath());
        List<String> directoryNames=getDirNames(rootDir);
        Log.e("Mytest","directoryNames:"+directoryNames);
        mAdapter=new MyFileItemAdapter(directoryNames);
        Log.e("Mytest","Adapter:"+mAdapter.toString());
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    private List<File> getDirs(File directory) {
        File[] files = directory.listFiles();
        List<File> dirs = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    dirs.add(file);
                }
            }
            return dirs;
        }

        return null;
    }

    private List<String> getDirNames(File directory) {
        File[] files = directory.listFiles();
        List<String> dirNames = new ArrayList<>();
        if (files!=null){
            for (File file : files) {
                if (file.isDirectory()){
                    dirNames.add(file.getName());
                }
            }
            Collections.sort(dirNames);
            return dirNames;
        }
        return null;
    }
    private void checkAndRequestReadPermission(){
        int REQUEST_READ_CODE=123;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            Log.e("Mytest","read permission not granted");
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_READ_CODE);

        }else if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            Log.e("Mytest","read permission has granted");
        }
    }
}
