package com.alox1d.vkvoicenotes.data.model

import com.alox1d.vkvoicenotes.domain.model.VoiceNote

class VoiceNoteMapper {
    fun mapToDomain(note: VoiceNoteDTO): VoiceNote {
        return VoiceNote(
            id = note.id,
            name = note.name,
            path = note.path,
            duration = note.duration,
            type = note.type,
            date = note.date,
            isPlaying = note.isPlaying
        )
    }

    fun mapToDTO(note: VoiceNote): VoiceNoteDTO {
        return VoiceNoteDTO(
            id = note.id,
            name = note.name,
            path = note.path,
            duration = note.duration,
            type = note.type,
            date = note.date,
            isPlaying = note.isPlaying
        )
    }
}
