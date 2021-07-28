package com.vladgroshkov.automatedlibrary.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

class UriUtil {
    companion object {
        fun getRealPathFromUri(context: Context, contentUri: Uri?): String? {
            var cursor: Cursor? = null
            return try {
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                cursor = context.getContentResolver().query(contentUri!!, proj, null, null, null)
                val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                cursor.getString(column_index)
            } finally {
                cursor?.close()
            }
        }
    }
}