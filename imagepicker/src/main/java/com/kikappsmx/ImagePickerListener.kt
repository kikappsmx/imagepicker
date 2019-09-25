package com.kikappsmx

import android.graphics.Bitmap

interface ImagePickerListener {
    fun onImageSelected(bitmap: Bitmap)
    fun onPermissionDenied()
    fun onPermissionBlocked()
}
