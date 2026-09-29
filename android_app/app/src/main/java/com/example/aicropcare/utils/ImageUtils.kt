package com.example.aicropcare.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageUtils {

    private const val TAG = "AI_TIMING"

    fun createTempImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.cacheDir
        return File.createTempFile("crop_leaf_${timeStamp}_", ".jpg", storageDir)
    }

    fun optimizeImageForAnalysis(
        context: Context,
        inputFile: File,
        maxDimension: Int = 1200,
        quality: Int = 82
    ): File {
        val startTime = System.currentTimeMillis()
        val originalLength = inputFile.length()

        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(inputFile.absolutePath, options)

            val rawWidth = options.outWidth
            val rawHeight = options.outHeight

            // If file is already small and bounded, return as is
            if (rawWidth > 0 && rawHeight > 0 && rawWidth <= maxDimension && rawHeight <= maxDimension && originalLength < 350 * 1024) {
                val durMs = System.currentTimeMillis() - startTime
                Log.d(TAG, "[AI TIMING] Image preprocessing: $durMs ms (Already optimal ${rawWidth}x${rawHeight}, ${originalLength / 1024} KB)")
                return inputFile
            }

            var inSampleSize = 1
            if (rawHeight > maxDimension || rawWidth > maxDimension) {
                val halfHeight = rawHeight / 2
                val halfWidth = rawWidth / 2
                while ((halfHeight / inSampleSize) >= maxDimension || (halfWidth / inSampleSize) >= maxDimension) {
                    inSampleSize *= 2
                }
            }

            val decodeOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize.coerceAtLeast(1)
                inPreferredConfig = Bitmap.Config.RGB_565 // Memory and speed optimization
            }

            val sampledBitmap = BitmapFactory.decodeFile(inputFile.absolutePath, decodeOptions)
            if (sampledBitmap == null) {
                Log.w(TAG, "Failed to decode sampled bitmap, falling back to original file.")
                return inputFile
            }

            // EXIF Orientation check
            var rotationAngle = 0f
            try {
                val exif = ExifInterface(inputFile.absolutePath)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                rotationAngle = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                    else -> 0f
                }
            } catch (ex: Exception) {
                Log.w(TAG, "EXIF read error: ${ex.message}")
            }

            // Calculate exact target dimensions within maxDimension
            val curW = sampledBitmap.width
            val curH = sampledBitmap.height
            val scale = if (curW > maxDimension || curH > maxDimension) {
                maxDimension.toFloat() / maxOf(curW, curH).toFloat()
            } else {
                1.0f
            }

            val matrix = Matrix()
            if (scale < 1.0f) {
                matrix.postScale(scale, scale)
            }
            if (rotationAngle != 0f) {
                matrix.postRotate(rotationAngle)
            }

            val finalBitmap = if (!matrix.isIdentity) {
                Bitmap.createBitmap(sampledBitmap, 0, 0, curW, curH, matrix, true)
            } else {
                sampledBitmap
            }

            val optimizedFile = createTempImageFile(context)
            FileOutputStream(optimizedFile).use { out ->
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            }

            if (finalBitmap != sampledBitmap) {
                finalBitmap.recycle()
            }
            sampledBitmap.recycle()

            val durMs = System.currentTimeMillis() - startTime
            val finalLength = optimizedFile.length()
            Log.d(TAG, "[AI TIMING] Image preprocessing: $durMs ms (Original: ${originalLength / 1024} KB -> Optimized: ${finalLength / 1024} KB, Target: ${finalBitmap.width}x${finalBitmap.height})")

            optimizedFile
        } catch (e: Exception) {
            Log.e(TAG, "Image optimization failed, using original file", e)
            inputFile
        }
    }

    fun copyUriToTempFile(context: Context, uri: Uri): File? {
        val startTime = System.currentTimeMillis()
        return try {
            val rawTempFile = createTempImageFile(context)
            val inputStream: InputStream = context.contentResolver.openInputStream(uri) ?: return null
            FileOutputStream(rawTempFile).use { output ->
                inputStream.use { input ->
                    input.copyTo(output)
                }
            }

            val optimizedFile = optimizeImageForAnalysis(context, rawTempFile)
            val durMs = System.currentTimeMillis() - startTime
            Log.d(TAG, "[AI TIMING] Gallery image load + optimize: $durMs ms (Size: ${optimizedFile.length() / 1024} KB)")
            optimizedFile
        } catch (e: Exception) {
            Log.e(TAG, "Failed to copy and optimize URI", e)
            null
        }
    }

    fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 -> String.format(Locale.getDefault(), "%.1f MB", bytes / (1024f * 1024f))
            bytes >= 1024 -> String.format(Locale.getDefault(), "%.1f KB", bytes / 1024f)
            else -> "$bytes B"
        }
    }
}


