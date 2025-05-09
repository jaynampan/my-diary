package meow.softer.mydiary.entries.diary

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import meow.softer.mydiary.R
import meow.softer.mydiary.shared.FileManager
import meow.softer.mydiary.shared.photo.BitmapHelper
import meow.softer.mydiary.shared.photo.ExifUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CopyPhotoTask : AsyncTask<Void?, Void?, String?> {
    interface CopyPhotoCallBack {
        fun onCopyCompiled(fileName: String?)
    }

    private var uri: Uri? = null
    private var srcFileName: String? = null
    private var progressDialog: ProgressDialog? = null
    private var callBack: CopyPhotoCallBack? = null
    private var mContext: Context? = null
    private var reqWidth = 0
    private var reqHeight = 0
    private var fileManager: FileManager? = null
    private var isAddPicture = false


    /**
     * From select image
     */
    constructor(
        context: Context, uri: Uri?,
        reqWidth: Int, reqHeight: Int,
        fileManager: FileManager, callBack: CopyPhotoCallBack
    ) {
        this.uri = uri
        isAddPicture = false
        initTask(context, reqWidth, reqHeight, fileManager, callBack)
    }


    /**
     * From take a picture
     */
    constructor(
        context: Context, srcFileName: String?,
        reqWidth: Int, reqHeight: Int,
        fileManager: FileManager, callBack: CopyPhotoCallBack
    ) {
        this.srcFileName = fileManager.dirAbsolutePath + "/" + srcFileName
        isAddPicture = true
        initTask(context, reqWidth, reqHeight, fileManager, callBack)
    }

    fun initTask(
        context: Context,
        reqWidth: Int, reqHeight: Int,
        fileManager: FileManager, callBack: CopyPhotoCallBack
    ) {
        this.mContext = context
        this.reqWidth = reqWidth
        this.reqHeight = reqHeight
        this.fileManager = fileManager
        this.callBack = callBack
        this.progressDialog = ProgressDialog(context)

        progressDialog!!.setMessage(context.getString(R.string.process_dialog_loading))
        progressDialog!!.setCancelable(false)
        progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
        progressDialog!!.show()
    }

    override fun doInBackground(vararg voids: Void?): String? {
        var returnFileName: String? = null
        try {
            //1.Create bitmap
            //2.Get uri exif
            if (isAddPicture) {
                returnFileName = savePhotoToTemp(
                    ExifUtil.rotateBitmap(
                        srcFileName!!,
                        BitmapHelper.getBitmapFromTempFileSrc(srcFileName, reqWidth, reqHeight)!!
                    )
                )
            } else {
                //rotateBitmap && resize
                returnFileName = savePhotoToTemp(
                    ExifUtil.rotateBitmap(
                        mContext, uri,
                        BitmapHelper.getBitmapFromReturnedImage(mContext!!, uri!!, reqWidth, reqHeight)!!
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("CopyPhotoTask", e.toString())
        }
        return returnFileName
    }

    override fun onPostExecute(srcFileName: String?) {
        Log.e("Mytest", "copyphoto onpost invoked")
        super.onPostExecute(srcFileName)
        progressDialog!!.dismiss()
        Log.e("Mytest", "copyphoto onpost srcFileName: $srcFileName")
        callBack!!.onCopyCompiled(srcFileName)
        Log.e("Mytest", "copyphoto onpost callback invoked")
    }

    private fun savePhotoToTemp(bitmap: Bitmap): String {
        //test start
        Log.e("Mytest", "copyphoto savephoto input bitmap size: " + bitmap.getByteCount())
        //test end
        var out: FileOutputStream? = null
        val fileName = FileManager.createRandomFileName()
        try {
            out = FileOutputStream(fileManager!!.dirAbsolutePath + "/" + fileName)
            Log.e(
                "Mytest",
                "copyphoto out path: " + fileManager!!.dirAbsolutePath + "/" + fileName
            )
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out) // bmp is your Bitmap instance
        } catch (e: Exception) {
            Log.e("Mytest", "copyphoto savephoto bitmap compress failed")
            e.printStackTrace()
        } finally {
            try {
                if (out != null) {
                    out.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        //test
        Log.e("Mytest", "copyphototask savephotototemp filename: $fileName")
        if (fileName != null) {
            val file = File(fileManager!!.dirAbsolutePath + "/" + fileName)
            val fileSize = file.length()
            Log.e("Mytest", "copyphototask savephoto filelength: $fileSize")
        } else {
            Log.e("Mytest", "copyphototask savephoto file is null")
        }
        //test
        return fileName
    }
}
