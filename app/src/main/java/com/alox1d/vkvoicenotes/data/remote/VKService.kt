package com.alox1d.vkvoicenotes.data.remote

import android.net.Uri
import com.alox1d.vkvoicenotes.data.model.VoiceNoteDTO
import com.vk.api.sdk.VK
import java.io.File

class VKService {

    private var temp1FileNote: File? = null

    fun uploadNote(note: VoiceNoteDTO) {
        val uploadNote = prepareNote(note)
        val uri = prepareUri(note)
        VK.executeSync(VKDocsCommand(uploadNote.name ?: "", uri))
        clean()
    }

    private fun clean() {
        temp1FileNote?.delete()
    }

    private fun prepareUri(note: VoiceNoteDTO): Uri {
        val fileNote = File(note.path)
        return Uri.fromFile(fileNote)
    }

    /*
    Конвертация записи для документов ВК (не принимают формат .AAC)
     */
    private fun prepareNote(note: VoiceNoteDTO): VoiceNoteDTO {
        val fileNote = File(note.path)
        val uploadNote = note.copy(
            name = note.name?.dropLast(1),
            path = note.path.dropLast(1)
        )
        File(uploadNote.path).let {
            temp1FileNote = it
            fileNote.copyTo(it, overwrite = true)
        }
        return uploadNote
    }
}