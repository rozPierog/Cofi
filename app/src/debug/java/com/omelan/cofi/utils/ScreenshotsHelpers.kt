package com.omelan.cofi.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.IOException

object ScreenshotsHelpers {
    @RequiresApi(Build.VERSION_CODES.Q)
    @Throws(IOException::class)
    fun saveBitmap(
        context: Context, bitmap: Bitmap, format: Bitmap.CompressFormat,
        mimeType: String, displayName: String
    ): Uri {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        var uri: Uri? = null

        return runCatching {
            with(context.contentResolver) {
                val selection = "${MediaStore.MediaColumns.RELATIVE_PATH}='Pictures/'"
                query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DISPLAY_NAME),
                    selection,
                    null,
                    null
                ).use {
                    if (it != null && it.count >= 1) {
                        for (i in 0 until it.count) {
                            it.moveToNext()
                            val id =
                                it.getLong(it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                            val name =
                                it.getString(it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                            if (name == displayName) {
                                val imageUri = ContentUris.withAppendedId(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    id
                                )
                                delete(imageUri, null, null)
                            }
                        }
                    }
                }
                insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)?.also {
                    uri = it // Keep uri reference so it can be removed on failure
                    openOutputStream(it)?.use { stream ->
                        if (!bitmap.compress(format, 95, stream)) {
                            throw IOException("Failed to save bitmap.")
                        }
                    } ?: throw IOException("Failed to open output stream.")
                } ?: throw IOException("Failed to create new MediaStore record.")
            }
        }.getOrElse {
            uri?.let { orphanUri ->
                context.contentResolver.delete(orphanUri, null, null)
            }

            throw it
        }
    }
}