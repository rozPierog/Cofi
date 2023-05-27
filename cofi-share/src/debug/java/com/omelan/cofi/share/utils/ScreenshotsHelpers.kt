package com.omelan.cofi.share.utils

import android.content.ContentResolver
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
    private fun getExistingImageUriOrNull(
        contentResolver: ContentResolver,
        searchedDisplayName: String,
    ): Uri? {
        val selection = "${MediaStore.MediaColumns.RELATIVE_PATH}='Pictures/' AND " +
            "${MediaStore.MediaColumns.DISPLAY_NAME}='$searchedDisplayName.png' "
        with(contentResolver) {
            query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.MediaColumns._ID),
                selection,
                null,
                null,
            ).use { c ->
                if (c != null && c.count >= 1) {
                    c.moveToFirst()
                    val id = c.getLong(c.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    return ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id,
                    )
                }
            }
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @Throws(IOException::class)
    fun saveBitmap(
        context: Context,
        bitmap: Bitmap,
        format: Bitmap.CompressFormat,
        mimeType: String,
        displayName: String,
    ): Uri {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        var uri: Uri? = null

        return runCatching {
            with(context.contentResolver) {
                val existingImage = getExistingImageUriOrNull(this, displayName)
                if (existingImage != null) {
                    delete(existingImage, null)
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
