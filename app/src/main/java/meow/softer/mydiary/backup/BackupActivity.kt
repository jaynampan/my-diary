package meow.softer.mydiary.backup

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.nononsenseapps.filepicker.FilePickerActivity
import com.nononsenseapps.filepicker.Utils
import meow.softer.mydiary.MainActivity
import meow.softer.mydiary.R
import meow.softer.mydiary.backup.ExportAsyncTask.ExportCallBack
import meow.softer.mydiary.backup.ImportAsyncTask.ImportCallBack
import meow.softer.mydiary.ui.components.DashedLine
import meow.softer.mydiary.ui.components.DiaryButton
import meow.softer.mydiary.ui.home.MainViewModel
import java.io.File

class BackupActivity : AppCompatActivity(), ExportCallBack, ImportCallBack {
    private val EXPORT_SRC_PICKER_CODE = 0
    private val IMPORT_SRC_PICKER_CODE = 1


    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column(
                modifier = Modifier
                    .padding(5.dp)

            ) {
                Text(text = stringResource(R.string.backup_title),
                    fontSize = 28.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top  = dimensionResource(R.dimen.setting_group_margin_top))
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.backup_export_title),
                    color = Color.Black,
                    fontSize = 22.sp
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = stringResource(R.string.backup_export_hint),
                    color = colorResource(R.color.backup_hint_text_color),
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.backup_export_path_hint),
                    color = colorResource(R.color.backup_hint_text_color),
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .padding(4.dp)
                        .clickable {
                            val exportIntent = Intent(
                                this@BackupActivity,
                                MyDirectoryPickerActivity::class.java
                            )

                            exportIntent.putExtra(
                                FilePickerActivity.EXTRA_ALLOW_MULTIPLE,
                                false
                            )
                            exportIntent.putExtra(
                                FilePickerActivity.EXTRA_ALLOW_CREATE_DIR,
                                true
                            )
                            exportIntent.putExtra(
                                FilePickerActivity.EXTRA_MODE,
                                FilePickerActivity.MODE_DIR
                            )
                            exportIntent.putExtra(
                                FilePickerActivity.EXTRA_START_PATH,
                                Environment.getExternalStorageDirectory().path
                            )
                            startActivityForResult(exportIntent, EXPORT_SRC_PICKER_CODE)
                        }
                )
                Spacer(Modifier.height(10.dp))
                DiaryButton(
                    onClick = {
                        ExportAsyncTask(
                            this@BackupActivity,
                            this@BackupActivity,
                            viewModel.getBackUpPath()
                        ).execute()
                    },
                    content = {
                        Text(stringResource(R.string.backup_export_button))
                    },
                )
                Spacer(Modifier
                    .height(2.dp)
                    .padding(horizontal = 5.dp)
                    .fillMaxWidth()

                )
                DashedLine(Modifier.fillMaxWidth())

                Text(
                    text = stringResource(R.string.backup_import_title),
                    color = Color.Black,
                    fontSize = 22.sp
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = stringResource(R.string.backup_import_hint),
                    color = colorResource(R.color.backup_hint_text_color),
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.backup_import_path_hint),
                    color = colorResource(R.color.backup_hint_text_color),
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .padding(4.dp)
                        .clickable {
                            val importIntent = Intent(this@BackupActivity, MyDirectoryPickerActivity::class.java)

                            importIntent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)
                            importIntent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false)
                            importIntent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE)
                            importIntent.putExtra(
                                FilePickerActivity.EXTRA_START_PATH,
                                Environment.getExternalStorageDirectory().path
                            )
                            startActivityForResult(importIntent, IMPORT_SRC_PICKER_CODE)
                        }
                )
                Spacer(Modifier.height(10.dp))
                DiaryButton(
                    onClick = {
                        ImportAsyncTask(
                            this@BackupActivity,
                            this@BackupActivity,
                            viewModel.getImportPath()
                        ).execute()
                    },
                    content = {
                        Text(stringResource(R.string.backup_import_button))
                    },
                )
                Spacer(Modifier
                    .height(2.dp)
                    .padding(horizontal = 5.dp)
                    .fillMaxWidth()

                )
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == EXPORT_SRC_PICKER_CODE && resultCode == RESULT_OK) {
            val uri = intent!!.data
            if (uri != null) {
                val file = Utils.getFileForUri(uri)
                if (file.canWrite()) {
                    viewModel.updateBackUpSrc(file.absolutePath)
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
                    viewModel.updateImportPath(file.absolutePath)
                } else {
                    Toast.makeText(
                        this, getString(R.string.backup_import_can_not_read),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
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


