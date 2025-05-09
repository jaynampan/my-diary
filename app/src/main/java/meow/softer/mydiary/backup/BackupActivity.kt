package meow.softer.mydiary.backup

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.nononsenseapps.filepicker.FilePickerActivity
import com.nononsenseapps.filepicker.Utils
import meow.softer.mydiary.MainActivity
import meow.softer.mydiary.R
import meow.softer.mydiary.backup.ExportAsyncTask.ExportCallBack
import meow.softer.mydiary.backup.ImportAsyncTask.ImportCallBack
import meow.softer.mydiary.shared.gui.MyDiaryButton
import java.io.File

class BackupActivity : AppCompatActivity(), View.OnClickListener, ExportCallBack, ImportCallBack {
    private val EXPORT_SRC_PICKER_CODE = 0
    private val IMPORT_SRC_PICKER_CODE = 1

    /*
     * UI
     */
    private var TV_backup_export_src: TextView? = null
    private var TV_backup_import_src: TextView? = null
    private var But_backup_export: MyDiaryButton? = null
    private var But_backup_import: MyDiaryButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup)

        //UI
        TV_backup_export_src = findViewById<TextView>(R.id.TV_backup_export_src)
        TV_backup_export_src!!.setOnClickListener(this)

        TV_backup_import_src = findViewById<TextView>(R.id.TV_backup_import_src)
        TV_backup_import_src!!.setOnClickListener(this)

        But_backup_export = findViewById<MyDiaryButton>(R.id.But_backup_export)
        But_backup_export!!.setOnClickListener(this)
        But_backup_export!!.setEnabled(false)

        But_backup_import = findViewById<MyDiaryButton>(R.id.But_backup_import)
        But_backup_import!!.setOnClickListener(this)
        But_backup_import!!.setEnabled(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == EXPORT_SRC_PICKER_CODE && resultCode == RESULT_OK) {
            val uri = intent!!.data
            if (uri != null) {
                val file = Utils.getFileForUri(uri)
                if (file.canWrite()) {
                    TV_backup_export_src!!.text = file.absolutePath
                    But_backup_export!!.setEnabled(true)
                } else {
                    Toast.makeText(
                        this, getString(R.string.backup_export_can_not_write),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else if (requestCode == IMPORT_SRC_PICKER_CODE && resultCode == RESULT_OK) {
            val uri = intent!!.data
            if (uri != null) {
                val file = Utils.getFileForUri(uri)
                if (file.canRead()) {
                    TV_backup_import_src!!.text = file.absolutePath
                    But_backup_import!!.setEnabled(true)
                } else {
                    Toast.makeText(
                        this, getString(R.string.backup_import_can_not_read),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.TV_backup_export_src -> {
                val exportIntent = Intent(this, MyDirectoryPickerActivity::class.java)

                exportIntent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)
                exportIntent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true)
                exportIntent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR)
                exportIntent.putExtra(
                    FilePickerActivity.EXTRA_START_PATH,
                    Environment.getExternalStorageDirectory().path
                )
                startActivityForResult(exportIntent, EXPORT_SRC_PICKER_CODE)
            }

            R.id.TV_backup_import_src -> {
                val importIntent = Intent(this, MyDirectoryPickerActivity::class.java)

                importIntent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)
                importIntent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false)
                importIntent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE)
                importIntent.putExtra(
                    FilePickerActivity.EXTRA_START_PATH,
                    Environment.getExternalStorageDirectory().path
                )
                startActivityForResult(importIntent, IMPORT_SRC_PICKER_CODE)
            }

            R.id.But_backup_export -> ExportAsyncTask(
                this,
                this,
                TV_backup_export_src!!.getText().toString()
            )
                .execute()

            R.id.But_backup_import -> ImportAsyncTask(
                this,
                this,
                TV_backup_import_src!!.getText().toString()
            )
                .execute()
        }
    }

    override fun onImportCompiled(importSuccessful: Boolean) {
        val backMainActivityIntent = Intent(this, MainActivity::class.java)
        backMainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(backMainActivityIntent)
    }

    override fun onExportCompiled(backupZipFilePath: String?) {
        //Open the shared file intent
        try {
            val sendIntent = Intent()
            if (backupZipFilePath != null) {
                val backupFile = File(backupZipFilePath)
                val backupFileUri = FileProvider.getUriForFile(
                    this,
                    this.applicationContext.packageName + ".provider",
                    backupFile
                )
                sendIntent.setAction(Intent.ACTION_SEND)
                sendIntent.putExtra(Intent.EXTRA_STREAM, backupFileUri)
                sendIntent.setType("application/zip")
                startActivity(
                    Intent.createChooser(
                        sendIntent,
                        getResources().getText(R.string.backup_export_share_title)
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("Backup", "export share fail", e)
        }
    }
}
