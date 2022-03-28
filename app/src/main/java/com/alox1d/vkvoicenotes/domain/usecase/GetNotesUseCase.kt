package com.android.musicplayer.domain.usecase

import com.alox1d.vkvoicenotes.data.model.VoiceNote
import com.android.musicplayer.domain.repository.VoiceListRepository
import javax.inject.Inject

class GetNotesUseCase @Inject constructor( val voiceListRepository: VoiceListRepository) {

    fun getAudios(): List<VoiceNote>? {
        return voiceListRepository.getSongs()
    }
}