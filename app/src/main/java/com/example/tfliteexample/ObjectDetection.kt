package com.example.tfliteexample

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector

object ObjectDetection {

    fun detect(context: Context, bitmap: Bitmap): List<Detection> {
        val optionsBuilder = ObjectDetector.ObjectDetectorOptions.builder()
            .setScoreThreshold(0.3F)
            .setMaxResults(8)
        val objectDetector = ObjectDetector.createFromFileAndOptions(
            context,
            "sehatin_modelV5b.tflite",
            optionsBuilder.build()
        )
        return objectDetector.detect(TensorImage.fromBitmap(bitmap))
    }

}