
package com.example.neurosense.recognition

import android.graphics.Bitmap
import android.graphics.Matrix
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.tasks.await
import kotlin.math.max
import kotlin.math.min

class FaceImageProcessor {

    private val detector =
        FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(
                    FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE
                )
                .setLandmarkMode(
                    FaceDetectorOptions.LANDMARK_MODE_NONE
                )
                .setClassificationMode(
                    FaceDetectorOptions.CLASSIFICATION_MODE_NONE
                )
                .setMinFaceSize(0.15f)
                .build()
        )

    // --------------------------------------------------
    // CROP FACE
    // --------------------------------------------------

    suspend fun cropFace(
        bitmap: Bitmap
    ): Bitmap? {

        if (
            bitmap.width <= 0 ||
            bitmap.height <= 0
        ) {
            return null
        }

        val inputImage =
            InputImage.fromBitmap(
                bitmap,
                0
            )

        val faces =
            try {

                detector
                    .process(inputImage)
                    .await()

            } catch (e: Exception) {

                e.printStackTrace()

                return null
            }

        // --------------------------------------------------
        // EXACTLY ONE FACE REQUIRED
        // --------------------------------------------------

        if (faces.size != 1) {
            return null
        }

        val face =
            faces[0]

        val bounds =
            face.boundingBox

        // --------------------------------------------------
        // FACE BOUNDARY VALIDATION
        // --------------------------------------------------

        if (
            bounds.width() <= 0 ||
            bounds.height() <= 0
        ) {
            return null
        }

        // --------------------------------------------------
        // ADD CONSISTENT MARGIN
        //
        // The same margin is used during:
        // Registration
        // Existing-user verification
        // --------------------------------------------------

        val marginX =
            (bounds.width() * 0.25f)
                .toInt()

        val marginY =
            (bounds.height() * 0.30f)
                .toInt()

        val left =
            max(
                0,
                bounds.left - marginX
            )

        val top =
            max(
                0,
                bounds.top - marginY
            )

        val right =
            min(
                bitmap.width,
                bounds.right + marginX
            )

        val bottom =
            min(
                bitmap.height,
                bounds.bottom + marginY
            )

        val width =
            right - left

        val height =
            bottom - top

        if (
            width <= 0 ||
            height <= 0
        ) {
            return null
        }

        // --------------------------------------------------
        // CREATE FACE CROP
        // --------------------------------------------------

        return try {

            Bitmap.createBitmap(
                bitmap,
                left,
                top,
                width,
                height
            )

        } catch (e: Exception) {

            e.printStackTrace()

            null
        }
    }

    // --------------------------------------------------
    // ROTATE BITMAP
    // --------------------------------------------------

    fun rotateBitmap(
        bitmap: Bitmap,
        rotationDegrees: Int
    ): Bitmap {

        if (
            rotationDegrees == 0
        ) {
            return bitmap
        }

        val matrix =
            Matrix().apply {

                postRotate(
                    rotationDegrees.toFloat()
                )
            }

        return try {

            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )

        } catch (e: Exception) {

            e.printStackTrace()

            bitmap
        }
    }

    // --------------------------------------------------
    // CLOSE
    // --------------------------------------------------

    fun close() {

        detector.close()
    }
}

