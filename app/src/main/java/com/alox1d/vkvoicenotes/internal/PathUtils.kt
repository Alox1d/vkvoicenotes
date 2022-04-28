package com.alox1d.vkvoicenotes.internal

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.lang.ref.WeakReference

object PathUtils {
    fun getPath(context: WeakReference<Context>, uri: Uri): String {
        if (uri.scheme == "file") {
            if (uri.path != null) return uri.path!!
            return ""
        }
        val proj = arrayOf(MediaStore.Images.Media.DATA)
         context.get()?.let {
             val cursor = it.contentResolver.query(uri, proj, null, null, null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return "file://" + cursor.getString(columnIndex)
        }
        return ""
    }
}