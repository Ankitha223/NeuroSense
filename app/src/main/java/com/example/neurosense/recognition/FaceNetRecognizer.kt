package com.example.neurosense.recognition

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sqrt

class FaceNetRecognizer(
    private val context: Context
) {

    companion object {
        private const val MODEL_NAME = "facenet.tflite"
        private const val INPUT_SIZE = 160
        private const val EMBEDDING_SIZE = 128
    }

    private val interpreter: Interpreter

    init {
        val modelBuffer = loadModelFile(MODEL_NAME)
        interpreter = Interpreter(modelBuffer)
    }

    /**
     * Loads facenet.tflite from:
     * app/src/main/assets/
     */
    private fun loadModelFile(fileName: String): ByteBuffer {

        val fileDescriptor = context.assets.openFd(fileName)

        val inputStream = fileDescriptor.createInputStream()

        val fileBytes = ByteArray(
            fileDescriptor.declaredLength.toInt()
        )

        inputStream.use { stream ->
            stream.read(fileBytes)
        }

        return ByteBuffer
            .allocateDirect(fileBytes.size)
            .order(ByteOrder.nativeOrder())
            .apply {
                put(fileBytes)
                rewind()
            }
    }

    /**
     * Converts a face Bitmap into a normalized
     * 128-dimensional FaceNet embedding.
     *
     * The input image is resized to 160 x 160.
     */
    fun getEmbedding(bitmap: Bitmap): FloatArray {

        val resizedBitmap = Bitmap.createScaledBitmap(
            bitmap,
            INPUT_SIZE,
            INPUT_SIZE,
            true
        )

        val inputBuffer = ByteBuffer
            .allocateDirect(
                INPUT_SIZE *
                        INPUT_SIZE *
                        3 *
                        4
            )
            .order(ByteOrder.nativeOrder())

        for (y in 0 until INPUT_SIZE) {

            for (x in 0 until INPUT_SIZE) {

                val pixel = resizedBitmap.getPixel(x, y)

                val red = (pixel shr 16) and 0xFF
                val green = (pixel shr 8) and 0xFF
                val blue = pixel and 0xFF

                /*
                 * FaceNet input normalization.
                 */
                inputBuffer.putFloat(
                    (red - 127.5f) / 128f
                )

                inputBuffer.putFloat(
                    (green - 127.5f) / 128f
                )

                inputBuffer.putFloat(
                    (blue - 127.5f) / 128f
                )
            }
        }

        inputBuffer.rewind()

        /*
         * FaceNet output:
         * [1][128]
         */
        val output = Array(1) {
            FloatArray(EMBEDDING_SIZE)
        }

        interpreter.run(
            inputBuffer,
            output
        )

        return normalizeEmbedding(output[0])
    }

    /**
     * L2-normalizes the FaceNet embedding.
     */
    private fun normalizeEmbedding(
        embedding: FloatArray
    ): FloatArray {

        var sum = 0f

        for (value in embedding) {
            sum += value * value
        }

        val magnitude = sqrt(sum)

        if (magnitude == 0f) {
            return embedding
        }

        return FloatArray(embedding.size) { index ->
            embedding[index] / magnitude
        }
    }

    /**
     * Calculates cosine similarity between
     * two FaceNet embeddings.
     *
     * Result:
     * closer to 1.0  -> more similar
     * closer to 0.0  -> less similar
     */
    fun cosineSimilarity(
        first: FloatArray,
        second: FloatArray
    ): Float {

        if (first.size != second.size) {
            return 0f
        }

        var dotProduct = 0f
        var magnitudeFirst = 0f
        var magnitudeSecond = 0f

        for (i in first.indices) {

            dotProduct +=
                first[i] * second[i]

            magnitudeFirst +=
                first[i] * first[i]

            magnitudeSecond +=
                second[i] * second[i]
        }

        if (
            magnitudeFirst == 0f ||
            magnitudeSecond == 0f
        ) {
            return 0f
        }

        return dotProduct /
                (
                        sqrt(magnitudeFirst) *
                                sqrt(magnitudeSecond)
                        )
    }

    /**
     * Releases the TensorFlow Lite interpreter.
     */
    fun close() {
        interpreter.close()
    }
}