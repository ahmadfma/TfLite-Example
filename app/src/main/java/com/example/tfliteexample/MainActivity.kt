package com.example.tfliteexample

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var imageResult: ImageView
    private lateinit var takePictureBtn: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageResult = findViewById(R.id.imageResult)
        takePictureBtn = findViewById(R.id.takePictureBtn)
        takePictureBtn.setOnClickListener {
            startIntentCamera()
        }
    }

    private fun startIntentCamera() {
        if(allCameraPermissionsGranted()) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            launcherIntentCamera.launch(intent)
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS_CAMERA,
                REQUEST_CODE_CAMERA
            )
        }
    }

    private fun allCameraPermissionsGranted() = REQUIRED_PERMISSIONS_CAMERA.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val imageBitmap = it.data?.extras?.get("data") as Bitmap
            imageResult.setImageBitmap(imageBitmap)
        }
    }

    companion object {
        private const val MAX_FONT_SIZE = 50F
        private const val TAG = "ObjectDetectionActivity"
        val REQUIRED_PERMISSIONS_CAMERA = arrayOf(android.Manifest.permission.CAMERA)
        const val REQUEST_CODE_CAMERA = 10
        const val AUTHOR = "com.app.sehatin"
    }

}