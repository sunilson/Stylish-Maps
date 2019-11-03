package at.sunilson.stylishmaps.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns.*
import androidx.core.net.toUri
import at.sunilson.stylishmaps.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.File.separator
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.jar.Manifest

interface BitmapUtils {
    suspend fun exportBitmap(uri: Uri): Boolean
    suspend fun cacheBitmap(bitmap: Bitmap): Uri
    suspend fun getBitmap(uri: Uri): Bitmap
}

class BitmapUtilsImplementation(private val context: Context) : BitmapUtils {

    override suspend fun exportBitmap(uri: Uri) = withContext(Dispatchers.IO) {
        if (!context.hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return@withContext false
        }

        val bitmap = BitmapFactory.decodeFile(uri.path)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues()
            values.put(
                MediaStore.Images.Media.TITLE,
                "StylishMapExport_${System.currentTimeMillis()}"
            )
            values.put(DISPLAY_NAME, "StylishMapExport_${System.currentTimeMillis()}")
            values.put(MIME_TYPE, "image/png")
            values.put(RELATIVE_PATH, DIRECTORY_PICTURES + separator + "StylishMaps")
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())

            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?.let {
                    context.contentResolver.openOutputStream(it).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                        return@withContext true
                    }
                }
        } else {
            val folder = File(
                getExternalStoragePublicDirectory(DIRECTORY_PICTURES),
                "StylishMaps"
            )
            if (!folder.exists()) folder.mkdirs()
            val targetFile = File(folder, "StylishMapExport_${System.currentTimeMillis()}.png")
            FileOutputStream(targetFile).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                mediaScanIntent.data = Uri.fromFile(targetFile)
                context.sendBroadcast(mediaScanIntent)
            }

            return@withContext true
        }

        false
    }

    override suspend fun cacheBitmap(bitmap: Bitmap) = withContext(Dispatchers.IO) {
        val imageFile = File(context.getExternalFilesDir(null), "${System.currentTimeMillis()}.jpg")

        FileOutputStream(imageFile).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }

        imageFile.toUri()
    }

    override suspend fun getBitmap(uri: Uri) = withContext(Dispatchers.IO) {
        FileInputStream(File(uri.path)).use { BitmapFactory.decodeStream(it) }
    }
}