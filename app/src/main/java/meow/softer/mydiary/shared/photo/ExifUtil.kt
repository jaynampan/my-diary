package meow.softer.mydiary.shared.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import meow.softer.mydiary.shared.FileManager
import java.io.IOException

object ExifUtil {
    //EXIF:Exchangeable Image File
    fun rotateBitmap(src: String, bitmap: Bitmap): Bitmap {
        try {
            val orientation = getExifOrientation(src)
            return rotate(bitmap, orientation)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    fun rotateBitmap(context: Context?, uri: Uri?, bitmap: Bitmap): Bitmap {
        val path = FileManager.getRealPathFromURI(context!!, uri!!)
        if (path == null) {
            return bitmap
        }
        return rotateBitmap(path, bitmap)
    }

    private fun rotate(bitmap: Bitmap, orientation: Int): Bitmap {
        if (orientation == 1) {
            return bitmap
        }

        val matrix = Matrix()
        when (orientation) {
            2 -> matrix.setScale(-1f, 1f)
            3 -> matrix.setRotate(180f)
            4 -> {
                matrix.setRotate(180f)
                matrix.postScale(-1f, 1f)
            }

            5 -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
            }

            6 -> matrix.setRotate(90f)
            7 -> {
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
            }

            8 -> matrix.setRotate(-90f)
            else -> return bitmap
        }

        try {
            val oriented = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix,
                true
            )
            bitmap.recycle()
            return oriented
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            return bitmap
        }
    }

    @Throws(IOException::class)
    private fun getExifOrientation(src: String): Int {
        var orientation = 1

        try {
            val exifInterface = ExifInterface(src)
            orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        return orientation
    }
}
