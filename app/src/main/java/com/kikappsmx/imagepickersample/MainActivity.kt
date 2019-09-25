package com.kikappsmx.imagepickersample

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.kikappsmx.ImagePicker
import com.kikappsmx.ImagePickerListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ImagePickerListener {

    private val imagePicker by lazy { ImagePicker(this, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        image.setOnClickListener { imagePicker.openSelector() }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        imagePicker.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        imagePicker.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onImageSelected(bitmap: Bitmap) {
        image.setImageBitmap(bitmap)
    }

    override fun onPermissionDenied() {
        Log.v(TAG, "Permission denied")
    }

    override fun onPermissionBlocked() {
        Log.v(TAG, "Permission blocked")
    }

    private companion object {
        val TAG = MainActivity::class.java.simpleName
    }
}
