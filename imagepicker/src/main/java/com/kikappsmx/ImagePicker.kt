package com.kikappsmx

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.provider.MediaStore.EXTRA_OUTPUT
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kikappsmx.imagepicker.R

class ImagePicker(
    private val activity: Activity,
    private val listener: ImagePickerListener
) {

    private val camera by lazy { CameraUtils(activity) }
    private val gallery by lazy { GalleryUtils() }

    fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        when {
            intent?.data != null && requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK -> {
                listener.onImageSelected(gallery.getBitmapFromGallery(activity, intent.data!!))
            }
            requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK -> {
                listener.onImageSelected(camera.getBitmapFromCamera())
            }
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            when {
                grantResults.firstOrNull() == PERMISSION_GRANTED ->{
                    openSelector()
                }
                ActivityCompat.shouldShowRequestPermissionRationale(activity, WRITE_EXTERNAL_STORAGE).not() -> {
                    listener.onPermissionBlocked()
                }
                grantResults.firstOrNull() == PackageManager.PERMISSION_DENIED -> {
                    listener.onPermissionBlocked()
                }
            }
        }
    }

    fun openSelector() {
        when (PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE) -> {
                AlertDialog.Builder(activity).apply {
                    setItems(R.array.source_types) { _, index ->
                        when (index) {
                            0 -> openCamera()
                            1 -> openGallery()
                        }
                    }
                    show()
                }
            }
            else -> {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_PERMISSION
                )
            }
        }
    }

    private fun openCamera() = Intent(ACTION_IMAGE_CAPTURE).apply {
        putExtra(EXTRA_OUTPUT, camera.getUri())
        activity.startActivityForResult(this, REQUEST_CODE_CAMERA)
    }

    private fun openGallery() = Intent(ACTION_PICK, EXTERNAL_CONTENT_URI).apply {
        type = TYPE
        activity.startActivityForResult(this, REQUEST_CODE_GALLERY)
    }

    private companion object {
        const val REQUEST_CODE_PERMISSION = 599
        const val REQUEST_CODE_GALLERY = 600
        const val REQUEST_CODE_CAMERA = 601
        const val TYPE = "image/*"
    }
}
