package com.alox1d.vkvoicenotes.internal.util

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.alox1d.vkvoicenotes.R
import java.io.File
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
    fun getDirectoryPath(context: Context): String? {
        if (isExternalStorageWritable()) {
            val file = File(
                context.getExternalFilesDir("")?.absolutePath,
                context.getString(R.string.recordings_folder_name)
            )
            if (!file.exists()) {
                file.mkdirs()
            }
            return file.absolutePath
        }
        return context.filesDir.absolutePath // use internal storage if external storage is not available
    }

    // External storage access NOT working at Android 11+ ¥ Доступ к внешнему хранилище
    // НЕ работает на Android 11+.
    // Возможно использовать MANAGER-разрешение, но оно требует обоснование в Google Play.
    fun commonRecordingsDirPath(FolderName: String = ""): File? {
        var dir: File? = null
        dir = when (Build.VERSION.SDK_INT) {
            in Build.VERSION_CODES.S..Int.MAX_VALUE -> {
                File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS)
                        .toString() + "/" + FolderName
                )
            }
            in Build.VERSION_CODES.R..Build.VERSION_CODES.S -> {
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS)
                        .toString() + "/" + FolderName
                )
            }
            else-> {
                File(Environment.getExternalStorageDirectory().toString() + "/" + FolderName)
            }

        }

        // Make sure the path directory exists.
        if (!dir.exists()) {
            // Make it, if it doesn't exit
            val success = dir.mkdirs()
            if (!success) {
                dir = null
            }
        }
        return dir
    }

    private fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }
}