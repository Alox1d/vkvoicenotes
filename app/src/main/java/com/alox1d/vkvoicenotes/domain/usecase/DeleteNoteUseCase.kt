package com.android.musicplayer.domain.usecase

import com.alox1d.vkvoicenotes.domain.model.VoiceNote
import com.android.musicplayer.domain.repository.VoiceListRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor( val voiceListRepository: VoiceListRepository) {

    fun deleteSongItem(song: VoiceNote) {
        voiceListRepository.delete(song)
    }
}