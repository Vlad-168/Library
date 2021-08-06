package com.vladgroshkov.automatedlibrary.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

class UriUtil {
    companion object {
        fun Context.drawableToUri(drawable: Int): Uri {
            return Uri.parse("android.resource://$packageName/$drawable")
        }
    }
}