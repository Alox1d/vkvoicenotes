package com.alox1d.vkvoicenotes.internal.util

import java.io.File

object FileUtils {
    fun getFile(filePath: String) =
        File(filePath)

    fun renameFile(
        filePath: String,
        fileName: String,
        newFileNameWithExt: String,
        oldFile: File
    ): String {
        val dirPath = filePath.removeSuffix(fileName)
        val newFilePath = dirPath + newFileNameWithExt
        val newFile = File(newFilePath)
        oldFile.renameTo(newFile)
        return newFilePath
    }

    fun generateFileNameAAC(newFileName: String) = "$newFileName.aac"
}