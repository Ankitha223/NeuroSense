
package com.example.neurosense.recognition

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sqrt

class FaceNetRecognizer(
    private val context: Context
) {

    companion object {

        private const val TAG = "FaceNetRecognizer"

        private const val MODEL_NAME =
            "facenet.tflite"

        private const val INPUT_SIZE =
            160

        private const val EMBEDDING_SIZE =
            128

        /*
         * FaceNet models commonly use:
         *
         * (pixel - 127.5) / 128
         *
         * Keep this consistent for both
         * registration and verification.
         */
        private const val IMAGE_MEAN =
            127.5f

        private const val IMAGE_STD =
            128f
    }

    private val interpreter: Interpreter

    init {

        val modelBuffer =
            loadModelFile(
                MODEL_NAME
            )

        interpreter =
            Interpreter(
                modelBuffer
            )

        Log.d(
            TAG,
            "FaceNet model loaded successfully."
        )

        Log.d(
            TAG,
            "Input size: $INPUT_SIZE x $INPUT_SIZE"
        )

        Log.d(
            TAG,
            "Embedding size: $EMBEDDING_SIZE"
        )
    }

    // --------------------------------------------------
    // LOAD MODEL
    // --------------------------------------------------

    private fun loadModelFile(
        fileName: String
    ): ByteBuffer {

        val fileDescriptor =
            context.assets.openFd(
                fileName
            )

        val inputStream =
            fileDescriptor.createInputStream()

        val fileBytes =
            ByteArray(
                fileDescriptor.declaredLength
                    .toInt()
            )

        inputStream.use { stream ->

            var offset = 0

            while (
                offset <
                fileBytes.size
            ) {

                val bytesRead =
                    stream.read(
                        fileBytes,
                        offset,
                        fileBytes.size - offset
                    )

                if (bytesRead <= 0) {
                    break
                }

                offset += bytesRead
            }
        }

        fileDescriptor.close()

        return ByteBuffer
            .allocateDirect(
                fileBytes.size
            )
            .order(
                ByteOrder.nativeOrder()
            )
            .apply {

                put(
                    fileBytes
                )

                rewind()
            }
    }

    // --------------------------------------------------
    // GENERATE EMBEDDING
    // --------------------------------------------------

    fun getEmbedding(
        bitmap: Bitmap
    ): FloatArray {

        require(
            !bitmap.isRecycled
        ) {
            "Bitmap has already been recycled."
        }

        if (
            bitmap.width <= 0 ||
            bitmap.height <= 0
        ) {

            throw IllegalArgumentException(
                "Invalid bitmap dimensions."
            )
        }

        /*
         * Convert to a predictable format.
         *
         * This avoids differences caused by bitmap
         * configuration between camera captures.
         */
        val argbBitmap =
            if (
                bitmap.config ==
                Bitmap.Config.ARGB_8888
            ) {

                bitmap

            } else {

                bitmap.copy(
                    Bitmap.Config.ARGB_8888,
                    false
                )
            }

        /*
         * Every face passed to FaceNet must become
         * exactly 160 x 160 pixels.
         */
        val resizedBitmap =
            Bitmap.createScaledBitmap(
                argbBitmap,
                INPUT_SIZE,
                INPUT_SIZE,
                true
            )

        val inputBuffer =
            ByteBuffer.allocateDirect(
                INPUT_SIZE *
                        INPUT_SIZE *
                        3 *
                        4
            ).order(
                ByteOrder.nativeOrder()
            )

        // --------------------------------------------------
        // BITMAP -> FLOAT INPUT
        // --------------------------------------------------

        for (y in 0 until INPUT_SIZE) {

            for (x in 0 until INPUT_SIZE) {

                val pixel =
                    resizedBitmap.getPixel(
                        x,
                        y
                    )

                val red =
                    (pixel shr 16) and 0xFF

                val green =
                    (pixel shr 8) and 0xFF

                val blue =
                    pixel and 0xFF

                inputBuffer.putFloat(
                    (red - IMAGE_MEAN) /
                            IMAGE_STD
                )

                inputBuffer.putFloat(
                    (green - IMAGE_MEAN) /
                            IMAGE_STD
                )

                inputBuffer.putFloat(
                    (blue - IMAGE_MEAN) /
                            IMAGE_STD
                )
            }
        }

        inputBuffer.rewind()

        // --------------------------------------------------
        // MODEL OUTPUT
        // --------------------------------------------------

        val output =
            Array(1) {
                FloatArray(
                    EMBEDDING_SIZE
                )
            }

        interpreter.run(
            inputBuffer,
            output
        )

        val embedding =
            output[0]

        if (
            embedding.size !=
            EMBEDDING_SIZE
        ) {

            throw IllegalStateException(
                "Unexpected FaceNet embedding size: " +
                        embedding.size
            )
        }

        val normalizedEmbedding =
            normalizeEmbedding(
                embedding
            )

        Log.d(
            TAG,
            "Embedding generated successfully: ${normalizedEmbedding.size} dimensions"
        )

        /*
         * Clean temporary bitmap if we created
         * a separate ARGB bitmap.
         */
        if (
            argbBitmap !== bitmap &&
            !argbBitmap.isRecycled
        ) {

            argbBitmap.recycle()
        }

        if (
            !resizedBitmap.isRecycled
        ) {

            resizedBitmap.recycle()
        }

        return normalizedEmbedding
    }

    // --------------------------------------------------
    // L2 NORMALIZATION
    // --------------------------------------------------

    private fun normalizeEmbedding(
        embedding: FloatArray
    ): FloatArray {

        var sum =
            0f

        for (
        value in embedding
        ) {

            sum +=
                value * value
        }

        val magnitude =
            sqrt(sum)

        if (
            magnitude <=
            0.000001f
        ) {

            Log.w(
                TAG,
                "FaceNet produced a zero-magnitude embedding."
            )

            return embedding.copyOf()
        }

        return FloatArray(
            embedding.size
        ) { index ->

            embedding[index] /
                    magnitude
        }
    }

    // --------------------------------------------------
    // COSINE SIMILARITY
    // --------------------------------------------------

    fun cosineSimilarity(
        first: FloatArray,
        second: FloatArray
    ): Float {

        if (
            first.size !=
            second.size
        ) {

            Log.w(
                TAG,
                "Embedding size mismatch: " +
                        "${first.size} vs ${second.size}"
            )

            return 0f
        }

        var dotProduct =
            0f

        var magnitudeFirst =
            0f

        var magnitudeSecond =
            0f

        for (
        i in first.indices
        ) {

            dotProduct +=
                first[i] *
                        second[i]

            magnitudeFirst +=
                first[i] *
                        first[i]

            magnitudeSecond +=
                second[i] *
                        second[i]
        }

        if (
            magnitudeFirst <=
            0.000001f ||
            magnitudeSecond <=
            0.000001f
        ) {

            Log.w(
                TAG,
                "Cannot calculate cosine similarity from zero embedding."
            )

            return 0f
        }

        val similarity =
            dotProduct /
                    (
                            sqrt(
                                magnitudeFirst
                            ) *
                                    sqrt(
                                        magnitudeSecond
                                    )
                            )

        Log.d(
            TAG,
            "Cosine similarity: $similarity"
        )

        return similarity
    }

    // --------------------------------------------------
    // CLOSE
    // --------------------------------------------------

    fun close() {

        interpreter.close()

        Log.d(
            TAG,
            "FaceNet interpreter closed."
        )
    }
}

