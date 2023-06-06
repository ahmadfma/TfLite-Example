package com.example.tfliteexample

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var imageResult: ImageView
    private lateinit var takePictureBtn: FloatingActionButton
    var currentPhotoPath = ""

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
            intent.resolveActivity(packageManager)
            createCustomTempFile(application).also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this@MainActivity,
                    "com.example.tfliteexample",
                    it
                )
                currentPhotoPath = it.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                launcherIntentCamera.launch(intent)
            }
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

    private val launcherIntentCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                imageResult.setImageBitmap(BitmapFactory.decodeFile(file.path))
                val result = ObjectDetection.detect(this, BitmapFactory.decodeFile(currentPhotoPath))
                if(result.isNotEmpty()) {
                    val resultToDisplay = result.map { detection ->
                        val category = detection.categories.first()
                        val text = "${category.label}, ${category.score.times(100).toInt()}%"
                        DetectionResult(detection.boundingBox, text)
                    }
                    val imageWithResult = drawDetectionResult(BitmapFactory.decodeFile(currentPhotoPath), resultToDisplay)
                    imageResult.setImageBitmap(imageWithResult)
                }
                Log.d(TAG, "$result")
            }
        }
    }

    private fun drawDetectionResult(bitmap: Bitmap, detectionResults: List<DetectionResult>): Bitmap {
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outputBitmap)
        val pen = Paint()
        pen.textAlign = Paint.Align.LEFT

        detectionResults.forEach {
            // draw bounding box
            pen.color = Color.RED
            pen.strokeWidth = 8F
            pen.style = Paint.Style.STROKE
            val box = it.boundingBox
            canvas.drawRect(box, pen)
            val tagSize = Rect(0, 0, 0, 0)
            // calculate the right font size
            pen.style = Paint.Style.FILL_AND_STROKE
            pen.color = Color.YELLOW
            pen.strokeWidth = 2F
            pen.textSize = MAX_FONT_SIZE
            pen.getTextBounds(it.text, 0, it.text.length, tagSize)
     /*       val fontSize: Float = pen.textSize * box.width() / tagSize.width()
            // adjust the font size so texts are inside the bounding box
            if (fontSize < pen.textSize) pen.textSize = fontSize*/
            pen.textSize = 65F
            var margin = (box.width() - tagSize.width()) / 2.0F
            if (margin < 0F) margin = 0F
            canvas.drawText(
                it.text, box.left + margin,
                box.top + tagSize.height().times(1F),
                pen
            )
        }
        return outputBitmap
    }

    companion object {
        private const val MAX_FONT_SIZE = 50F
        private const val TAG = "ObjectDetectionActivity"
        val REQUIRED_PERMISSIONS_CAMERA = arrayOf(android.Manifest.permission.CAMERA)
        const val REQUEST_CODE_CAMERA = 10
        const val AUTHOR = "com.example.tfliteexample"
    }

}