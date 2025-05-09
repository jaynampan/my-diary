package meow.softer.mydiary.shared

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.os.StatFs
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import meow.softer.mydiary.main.topic.ITopic
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URI
import java.net.URISyntaxException
import java.util.Locale
import java.util.UUID
import androidx.core.net.toUri

class FileManager {
    /**
     * The path is :
     * 1.setting , topic bg & profile photo  temp
     * /sdcard/Android/data/com.example.mydiary/files/temp
     * 2.diary edit temp
     * /sdcard/Android/data/com.example.mydiary/files/diary/editCache
     * 3.diary saved
     * /sdcard/Android/data/com.example.mydiary/files/diary/TOPIC_ID/DIARY_ID/
     * 4.memo path
     * /sdcard/Android/data/com.example.mydiary/files/memo/TOPIC_ID/
     * 5.contacts path
     * /sdcard/Android/data/com.example.mydiary/files/contacts/TOPIC_ID/
     * 6.Setting path
     * /sdcard/Android/data/com.example.mydiary/files/setting/
     * 7.Backup temp path
     * /sdcard/Android/data/com.example.mydiary/files/backup/
     */
    var dir: File? = null
        private set
    private val mContext: Context

    constructor(context: Context, dir: Int) {
        this.mContext = context
        when (dir) {
            ROOT_DIR -> this.dir = mContext.getExternalFilesDir("")
            TEMP_DIR -> this.dir = mContext.getExternalFilesDir(TEMP_DIR_STR)
            DIARY_ROOT_DIR -> this.dir = mContext.getExternalFilesDir(DIARY_ROOT_DIR_STR)
            MEMO_ROOT_DIR -> this.dir = mContext.getExternalFilesDir(MEMO_ROOT_DIR_STR)
            CONTACTS_ROOT_DIR -> this.dir = mContext.getExternalFilesDir(CONTACTS_ROOT_DIR_STR)
            DIARY_EDIT_CACHE_DIR -> this.dir =
                mContext.getExternalFilesDir(EDIT_CACHE_DIARY_DIR_STR)

            SETTING_DIR -> this.dir = mContext.getExternalFilesDir(SETTING_DIR_STR)
            BACKUP_DIR -> this.dir = mContext.getExternalFilesDir(BACKUP_DIR_STR)
        }
    }

    /**
     * Create diary  dir file manager
     */
    constructor(context: Context, topicId: Long, diaryId: Long) {
        this.mContext = context
        this.dir =
            mContext.getExternalFilesDir("$DIARY_ROOT_DIR_STR/$topicId/$diaryId/")
    }

    /**
     * Create diary temp file manager for auto save
     * /sdcard/Android/data/com.example.mydiary/files/diary/TOPIC_ID/temp
     */
    constructor(context: Context, diaryTopicId: Long) {
        this.mContext = context
        this.dir = mContext.getExternalFilesDir("$DIARY_ROOT_DIR_STR/$diaryTopicId/temp/")
    }

    /**
     * Create topic dir file manager for delete
     */
    constructor(context: Context, topicType: Int, topicId: Long) {
        this.mContext = context
        when (topicType) {
            ITopic.TYPE_MEMO -> this.dir = mContext.getExternalFilesDir(
                "$MEMO_ROOT_DIR_STR/$topicId/"
            )

            ITopic.TYPE_CONTACTS -> this.dir =
                mContext.getExternalFilesDir("$CONTACTS_ROOT_DIR_STR/$topicId/")

            ITopic.TYPE_DIARY -> this.dir =
                mContext.getExternalFilesDir("$DIARY_ROOT_DIR_STR/$topicId/")

        }
    }


    val dirAbsolutePath: String
        get() = dir!!.absolutePath

    fun clearDir() {
        val fList = dir!!.listFiles()
        if (fList != null && dir!!.isDirectory()) {
            try {
                FileUtils.cleanDirectory(this.dir)
            } catch (e: IOException) {
                Log.e(TAG, "ClearDir file", e)
            }
        }
    }

    companion object {
        private const val TAG = "FileManager"

        //Min free space is 50 MB
        const val MIN_FREE_SPACE: Int = 100
        const val FILE_HEADER: String = "file://"

        const val ROOT_DIR: Int = 0
        const val TEMP_DIR: Int = 1
        const val DIARY_EDIT_CACHE_DIR: Int = 2
        const val DIARY_ROOT_DIR: Int = 3
        const val MEMO_ROOT_DIR: Int = 4
        const val CONTACTS_ROOT_DIR: Int = 5
        const val SETTING_DIR: Int = 6
        const val BACKUP_DIR: Int = 7
        private const val TEMP_DIR_STR = "temp/"
        private const val DIARY_ROOT_DIR_STR = "diary/"
        private const val MEMO_ROOT_DIR_STR = "memo/"
        private const val CONTACTS_ROOT_DIR_STR = "contacts/"
        private const val EDIT_CACHE_DIARY_DIR_STR = "diary/editCache/"
        private const val SETTING_DIR_STR = "setting/"
        private const val BACKUP_DIR_STR = "backup/"

        fun getFileNameByUri(context: Context, uri: Uri): String? {
            var displayName: String? = ""
            if (uri.scheme.toString().startsWith("content")) {
                val cursor = context.contentResolver
                    .query(uri, null, null, null, null, null)
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        val displayIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (displayIndex >= 0) {
                            displayName = cursor.getString(displayIndex)
                        } else {
                            Log.e(
                                TAG,
                                "cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME) got negative number!"
                            )
                        }
                        cursor.close()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                    e.printStackTrace()
                } finally {
                    cursor?.close()
                }
            } else if (uri.scheme.toString().startsWith("file")) {
                try {
                    val file = File(URI(uri.toString()))
                    if (file.exists()) {
                        displayName = file.getName()
                    }
                } catch (e: URISyntaxException) {
                    Log.e(TAG, e.toString())
                    e.printStackTrace()
                }
            } else {
                val file = File(uri.path)
                if (file.exists()) {
                    displayName = file.getName()
                }
            }
            Log.e("Mytest", "filemanager getfilenamebyuri displayname: $displayName")
            return displayName
        }

        fun startBrowseImageFile(activity: Activity, requestCode: Int) {
            try {
                val intentImage = Intent()
                intentImage.setType("image/*")
                intentImage.setAction(Intent.ACTION_GET_CONTENT)
                activity.startActivityForResult(
                    Intent.createChooser(intentImage, "Select Picture"),
                    requestCode
                )
                Log.e("Mytest", "Filemanager started to select pic,requestcode:$requestCode")
            } catch (ex: ActivityNotFoundException) {
                Log.e("Mytest", "Filemanager startbrowseimagefile failed")
                Log.e(TAG, ex.toString())
            }
        }

        fun startBrowseImageFile(fragment: Fragment, requestCode: Int) {
            try {
                val intentImage = Intent()
                intentImage.setType("image/*")
                intentImage.setAction(Intent.ACTION_GET_CONTENT)
                fragment.startActivityForResult(
                    Intent.createChooser(intentImage, "Select Picture"),
                    requestCode
                )
                Log.e("Mytest", "Filemanager started to select pic,requestcode:$requestCode")
            } catch (ex: ActivityNotFoundException) {
                Log.e("Mytest", "Filemanager startbrowseimagefile failed")
                Log.e(TAG, ex.toString())
            }
        }

        fun createRandomFileName(): String {
            return UUID.randomUUID().toString()
            //UUID:universally unique identifier可用于生成唯一的文件名或路径
        }

        fun isImage(fileName: String): Boolean {
            return fileName.lowercase(Locale.getDefault()).endsWith(".jpeg") || fileName.lowercase(
                Locale.getDefault()
            ).endsWith(".jpg") || fileName.lowercase(Locale.getDefault()).endsWith(".png")
        }

        @Throws(IOException::class)
        fun copy(src: File?, dst: File?) {
            val `in`: InputStream = FileInputStream(src)
            val out: OutputStream = FileOutputStream(dst)

            // Transfer bytes from in to out
            val buf = ByteArray(1024)
            var len: Int
            while ((`in`.read(buf).also { len = it }) > 0) {
                out.write(buf, 0, len)
            }
            `in`.close()
            out.close()
        }

        fun isNumeric(str: String): Boolean {
            return str.matches("-?\\d+(\\.\\d+)?".toRegex()) //match a number with optional '-' and decimal.
        }

        /**
         * Gets the real path from file
         *
         * @param context
         * @param uri
         * @return path
         */
        fun getRealPathFromURI(context: Context, uri: Uri): String? {
            //DocumentProvider
            if (DocumentsContract.isDocumentUri(context, uri)) {
                //External Storage Provider
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split: Array<String?> =
                        docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }
                } else if (isDownloadsDocument(uri)) {
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        "content://downloads/public_downloads".toUri(),
                        id.toLong()
                    )

                    return getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split: Array<String?> =
                        docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }

                    val selection = "_id=?"
                    val selectionArgs = arrayOf<String?>(
                        split[1]
                    )

                    return Companion.getDataColumn(context, contentUri!!, selection, selectionArgs)
                }
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                return getDataColumn(context, uri, null, null)
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
            return null
        }

        fun getDataColumn(
            context: Context, uri: Uri, selection: String?,
            selectionArgs: Array<String?>?
        ): String? {
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf<String?>(
                column
            )

            try {
                cursor = context.contentResolver.query(
                    uri, projection, selection, selectionArgs,
                    null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    val column_index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(column_index)
                }
            } finally {
                cursor?.close()
            }
            return null
        }

        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        val sDCardFreeSize: Long
            /**
             * @return MB
             */
            get() {
                val path = Environment.getExternalStorageDirectory()
                val sf = StatFs(path.path)
                val blockSize: Long = sf.blockSizeLong
                val freeBlocks: Long = sf.availableBlocksLong

                return (freeBlocks * blockSize) / 1024 / 1024
            }
    }
}
