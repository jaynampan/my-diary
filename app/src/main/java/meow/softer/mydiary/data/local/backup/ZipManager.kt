package meow.softer.mydiary.data.local.backup

import android.content.ContentValues
import android.content.Context
import android.util.Log
import meow.softer.mydiary.shared.FileManager
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class ZipManager(context: Context) {
    //Copy data form diary
    private val diaryFileManager: FileManager = FileManager(context, FileManager.DIARY_ROOT_DIR)
    private val BUFFER_SIZE = 2048

    fun zipFileAtPath(backupJsonFilePath: String?, toLocation: String?): Boolean {
        val sourceFile = diaryFileManager.dir
        try {
            var origin: BufferedInputStream?
            val dest = FileOutputStream(toLocation)
            val out = ZipOutputStream(
                BufferedOutputStream(
                    dest
                )
            )
            if (sourceFile!!.isDirectory()) {
                zipSubFolder(out, sourceFile, sourceFile.getParent().length)
            } else {
                val data: ByteArray? = ByteArray(BUFFER_SIZE)
                val fi = FileInputStream(sourceFile)
                origin = BufferedInputStream(fi, BUFFER_SIZE)
                val entry = ZipEntry(sourceFile.getName())
                out.putNextEntry(entry)
                var count: Int
                while ((origin.read(data, 0, BUFFER_SIZE).also { count = it }) != -1) {
                    out.write(data, 0, count)
                }
            }
            //Zip the json file
            zipBackupJsonFile(backupJsonFilePath, out)

            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    @Throws(IOException::class)
    private fun zipBackupJsonFile(backupJsonFilePath: String?, out: ZipOutputStream) {
        val data: ByteArray? = ByteArray(BUFFER_SIZE)
        val fi = FileInputStream(backupJsonFilePath)
        val jsonFileOrigin = BufferedInputStream(fi, BUFFER_SIZE)
        val entry = ZipEntry(BackupManager.BACKUP_JSON_FILE_NAME)
        out.putNextEntry(entry)
        var count: Int
        while ((jsonFileOrigin.read(data, 0, BUFFER_SIZE).also { count = it }) != -1) {
            out.write(data, 0, count)
        }
    }

    /**
     * Zips a subfolder
     */
    @Throws(IOException::class)
    private fun zipSubFolder(
        out: ZipOutputStream, folder: File,
        basePathLength: Int
    ) {
        val fileList = folder.listFiles()
        var origin: BufferedInputStream?
        for (file in fileList!!) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength)
            } else {
                val data: ByteArray? = ByteArray(BUFFER_SIZE)
                val unmodifiedFilePath = file.path
                val relativePath = unmodifiedFilePath
                    .substring(basePathLength)
                val fi = FileInputStream(unmodifiedFilePath)
                origin = BufferedInputStream(fi, BUFFER_SIZE)
                val entry = ZipEntry(relativePath)
                out.putNextEntry(entry)
                var count: Int
                while ((origin.read(data, 0, BUFFER_SIZE).also { count = it }) != -1) {
                    out.write(data, 0, count)
                }
                origin.close()
            }
        }
    }

    @Throws(IOException::class)
    fun unzip(backupZieFilePath: String?, location: String) {
        var location = location
        var size: Int
        val buffer = ByteArray(BUFFER_SIZE)

        try {
            if (!location.endsWith("/")) {
                location += "/"
            }
            val f = File(location)
            if (!f.isDirectory()) {
                f.mkdirs()
            }
            val zin = ZipInputStream(
                BufferedInputStream(
                    FileInputStream(backupZieFilePath), BUFFER_SIZE
                )
            )
            try {
                var ze: ZipEntry? = null
                while ((zin.getNextEntry().also { ze = it }) != null) {
                    val path = location + ze!!.name
                    val unzipFile = File(path)

                    if (ze.isDirectory) {
                        if (!unzipFile.isDirectory()) {
                            unzipFile.mkdirs()
                        }
                    } else {
                        // check for and create parent directories if they don't exist
                        val parentDir = unzipFile.getParentFile()
                        if (null != parentDir) {
                            if (!parentDir.isDirectory()) {
                                parentDir.mkdirs()
                            }
                        }

                        // unzip the file
                        val out = FileOutputStream(unzipFile, false)
                        val fout = BufferedOutputStream(out, BUFFER_SIZE)
                        try {
                            while ((zin.read(buffer, 0, BUFFER_SIZE).also { size = it }) != -1) {
                                fout.write(buffer, 0, size)
                            }

                            zin.closeEntry()
                        } finally {
                            fout.flush()
                            fout.close()
                        }
                    }
                }
            } finally {
                zin.close()
            }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Unzip exception", e)
        }
    }
}
