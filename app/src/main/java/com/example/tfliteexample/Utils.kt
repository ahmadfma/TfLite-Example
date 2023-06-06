package com.example.tfliteexample

import android.content.Context
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

val timeStamp: String = SimpleDateFormat(
    "ddMMyyyy",
    Locale.US
).format(System.currentTimeMillis())

// Untuk kasus Intent Camera
fun createCustomTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}