package meow.softer.mydiary.shared.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

object BitmapHelper {
    @Throws(IOException::class)
    fun getBitmapFromReturnedImage(
        context: Context,
        selectedImage: Uri,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {
        var inputStream = context.contentResolver.openInputStream(selectedImage)
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(inputStream, null, options)

        // Calculate inSampleSize
        options.inScaled = true
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        options.inPreferredConfig = Bitmap.Config.RGB_565

        // close the input stream
        inputStream!!.close()

        // reopen the input stream
        inputStream = context.contentResolver.openInputStream(selectedImage)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
        inputStream!!.close()
        //test start
        if (bitmap != null) {
            val bitmapSize = bitmap.getByteCount()
            Log.e("Mytest", "bitmaphelper getBitmapFromReturnedImage bitmap size: $bitmapSize")
        } else {
            Log.e("Mytest", "bitmaphelper getBitmapFromReturnedImage bitmap is null")
        }
        //test end
        return bitmap
    }

    @Throws(IOException::class)
    fun getBitmapFromTempFileSrc(tempFileSrc: String?, reqWidth: Int, reqHeight: Int): Bitmap? {
        var inputStream: InputStream = FileInputStream(tempFileSrc)
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(inputStream, null, options)

        // Calculate inSampleSize
        options.inScaled = true
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        options.inPreferredConfig = Bitmap.Config.RGB_565

        // close the input stream
        inputStream.close()

        // reopen the input stream
        inputStream = FileInputStream(tempFileSrc)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
        inputStream.close()
        return bitmap
    }

    /**
     * Note: The decoder uses a final value based on powers of 2,
     * any other value will be rounded down to the nearest power of 2.
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth

        var inSampleSize = 1
        try {
            if ((height > reqHeight || width > reqWidth) && options.outHeight > 0 && options.outWidth > 0) {
                val halfHeight = height / 2
                val halfWidth = width / 2

                //Old version (version =< 29):
                //  Choose the max ratio as inSampleSize value, I hope it can show fully without scrolling
                //New version (Version > 30)
                //  This bitmap is only for making photo size small ,
                // The only show one page method is implemented by fresco
                while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth
                ) {
                    inSampleSize *= 2
                }
                // This offers some additional logic in case the image has a strange
                // aspect ratio. For example, a panorama may have a much larger
                // width than height. In these cases the total pixels might still
                // end up being too large to fit comfortably in memory, so we should
                // be more aggressive with sample down the image (=larger inSampleSize).
                val totalPixels = (width * height).toFloat()

                // Anything more than 2x the requested pixels we'll sample down further
                val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()

                while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                    inSampleSize *= 2
                }
            }
        } catch (e: Exception) {
            //For avoid crash
            e.printStackTrace()
            inSampleSize = 1
        }
        return inSampleSize
    }
}
