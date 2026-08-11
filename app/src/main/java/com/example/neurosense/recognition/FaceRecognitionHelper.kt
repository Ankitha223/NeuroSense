package com.example.neurosense.recognition

import android.graphics.Bitmap
import android.graphics.Rect
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.tasks.await

class FaceRecognitionHelper {

    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(
                FaceDetectorOptions.PERFORMANCE_MODE_FAST
            )
            .build()
    )

    suspend fun detectFace(
        bitmap: Bitmap
    ): Bitmap? {

        val image = InputImage.fromBitmap(
            bitmap,
            0
        )

        val faces = detector
            .process(image)
            .await()

        if (faces.isEmpty()) {
            return null
        }

        val face = faces[0]

        val bounds = face.boundingBox

        val left = bounds.left.coerceAtLeast(0)
        val top = bounds.top.coerceAtLeast(0)

        val right = bounds.right.coerceAtMost(bitmap.width)
        val bottom = bounds.bottom.coerceAtMost(bitmap.height)

        if (right <= left || bottom <= top) {
            return null
        }

        return Bitmap.createBitmap(
            bitmap,
            left,
            top,
            right - left,
            bottom - top
        )
    }

    fun close() {
        detector.close()
    }
}