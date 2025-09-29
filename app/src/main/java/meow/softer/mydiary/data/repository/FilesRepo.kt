package meow.softer.mydiary.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meow.softer.mydiary.util.debug
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class FilesRepo @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    val userPictureFile = "saved_user_profile.jpg"

    suspend fun saveUserPic(uri: Uri): Bitmap? {
        saveFile(uri, userPictureFile)
        return getBitmap(uri)

    }

    suspend fun getUserPic(): Bitmap? {
        return withContext(Dispatchers.IO) {
            val profilePic = File(context.filesDir, userPictureFile)
            if (profilePic.exists()) {
                debug("FilesRepo", "getUserPic called, and file exits")
                BitmapFactory.decodeFile(profilePic.absolutePath)
            } else {
                null
            }
        }

    }

    suspend fun getBitmap(uri: Uri): Bitmap? {
        try {
            return withContext(Dispatchers.IO) {
                BitmapFactory.decodeStream(
                    context.contentResolver.openInputStream(uri)
                )
            }
        } catch (_: Exception) {

        }
        return null
    }

    private suspend fun saveFile(uri: Uri, fileName: String) {
        withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val profileFile = File(context.filesDir, fileName)
                val outputStream = FileOutputStream(profileFile)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
            } catch (_: Exception) {

            }

        }
    }

    fun saveUserPicBitmap(bitmap: Bitmap) {
        val file = File(context.filesDir, userPictureFile)
        FileOutputStream(file).use { outputStream ->
            // Compress bitmap to JPEG (or PNG if preferred)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
        }
    }
}