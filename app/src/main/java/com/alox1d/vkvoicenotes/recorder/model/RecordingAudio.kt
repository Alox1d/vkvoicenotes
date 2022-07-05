package com.alox1d.vkvoicenotes.recorder.model

data class RecordingAudio(
    val fileName: String,
    val filePath: String,
    val elapsedMillis: Long,
    val createdMillis: Long
)
