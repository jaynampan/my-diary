package meow.softer.mydiary.data.local.backup

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import meow.softer.mydiary.R
import java.io.File

class MyDirectoryPickerActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var mAdapter: MyFileItemAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var tv_current_dir: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_filepicker)
        recyclerView = findViewById<RecyclerView?>(R.id.RV_filepicker)
        tv_current_dir = findViewById<TextView?>(R.id.my_current_dir)

        checkAndRequestReadPermission()

        val rootDir = Environment.getExternalStorageDirectory()
        tv_current_dir!!.text = rootDir.path
        Log.e("Mytest", "root dir:" + rootDir.path)
        val directoryNames = getDirNames(rootDir)
        Log.e("Mytest", "directoryNames:$directoryNames")
        mAdapter = MyFileItemAdapter(directoryNames!! as MutableList<String?>)
        Log.e("Mytest", "Adapter:$mAdapter")
        layoutManager = LinearLayoutManager(this)
        recyclerView!!.setLayoutManager(layoutManager)
        recyclerView!!.setAdapter(mAdapter)
    }

    private fun getDirs(directory: File): MutableList<File?>? {
        val files = directory.listFiles()
        val dirs: MutableList<File?> = ArrayList<File?>()
        if (files != null) {
            for (file in files) {
                if (file.isDirectory()) {
                    dirs.add(file)
                }
            }
            return dirs
        }

        return null
    }

    private fun getDirNames(directory: File): MutableList<String>? {
        val files = directory.listFiles()
        val dirNames: MutableList<String> = ArrayList<String>()
        if (files != null) {
            for (file in files) {
                if (file.isDirectory()) {
                    dirNames.add(file.getName())
                }
            }
            dirNames.sort<String>()
            return dirNames
        }
        return null
    }

    private fun checkAndRequestReadPermission() {
        val REQUEST_READ_CODE = 123
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("Mytest", "read permission not granted")
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_READ_CODE
            )
        } else if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("Mytest", "read permission has granted")
        }
    }
}