package com.kikappsmx

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.ExifInterface.*
import android.net.Uri
import android.os.Environment
import android.os.Environment.DIRECTORY_DCIM
import android.os.Environment.DIRECTORY_PICTURES
import android.os.StrictMode
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CameraUtils(private val context: Context) {

    private lateinit var file: File
    private lateinit var uri: Uri

    fun getBitmapFromCamera(): Bitmap {
        val exifInterface = ExifInterface("${uri.host}${uri.path}")
        val orientation = exifInterface.getAttributeInt(TAG_ORIENTATION, ORIENTATION_UNDEFINED)
        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        val rotatedBitmap = when (orientation) {
            ORIENTATION_ROTATE_270 -> rotateImage(bitmap, DEGREE_270)
            ORIENTATION_ROTATE_180 -> rotateImage(bitmap, DEGREE_180)
            ORIENTATION_ROTATE_90 -> rotateImage(bitmap, DEGREE_90)
            else -> bitmap
        }

        FileOutputStream(file).use {
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, it)
        }

        return rotatedBitmap
    }

    fun getUri(): Uri {
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().build())
        val folder = Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM)
        if (folder.exists().not()) folder.mkdirs()
        val timeStamp = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
        file = File(folder.path + File.separator + PREFIX + timeStamp + POSTFIX)
        uri = Uri.fromFile(file)
        return uri
    }

    private fun rotateImage(bitmap: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(
            bitmap,
            COORDINATE,
            COORDINATE,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    private companion object {
        const val DATE_FORMAT = "yyyyMMdd_HHmmss"
        const val POSTFIX = ".jpg"
        const val PREFIX = "IMG_"
        const val DEGREE_270 = 170F
        const val DEGREE_180 = 180F
        const val DEGREE_90 = 90F
        const val COORDINATE = 0
        const val QUALITY = 100
    }
}
