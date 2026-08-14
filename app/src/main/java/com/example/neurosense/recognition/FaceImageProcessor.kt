package com.example.neurosense.recognition

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.tasks.await
import kotlin.math.max
import kotlin.math.min

class FaceImageProcessor {

    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(
                FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE
            )
            .build()
    )

    suspend fun cropFace(bitmap: Bitmap): Bitmap? {

        val inputImage = InputImage.fromBitmap(bitmap, 0)

        val faces = detector.process(inputImage).await()

        // No face or more than one face
        if (faces.size != 1) {
            return null
        }

        val face = faces[0]

        val bounds = face.boundingBox

        // Add a little margin around the detected face
        val marginX = (bounds.width() * 0.20f).toInt()
        val marginY = (bounds.height() * 0.25f).toInt()

        val left = max(0, bounds.left - marginX)
        val top = max(0, bounds.top - marginY)

        val right = min(
            bitmap.width,
            bounds.right + marginX
        )

        val bottom = min(
            bitmap.height,
            bounds.bottom + marginY
        )

        val width = right - left
        val height = bottom - top

        if (width <= 0 || height <= 0) {
            return null
        }

        return Bitmap.createBitmap(
            bitmap,
            left,
            top,
            width,
            height
        )
    }

    fun close() {
        detector.close()
    }
}