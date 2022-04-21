package com.android.musicplayer.domain.usecase

import com.alox1d.vkvoicenotes.domain.model.VoiceNote
import com.android.musicplayer.domain.repository.VoiceListRepository
import javax.inject.Inject

class  SaveNoteDataUseCase @Inject constructor(var voiceListRepository: VoiceListRepository) {

    fun saveNoteItem(note: VoiceNote) {
        voiceListRepository.saveSongData(note)
    }
}