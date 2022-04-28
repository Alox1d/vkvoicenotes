package com.android.musicplayer.domain.usecase

import com.alox1d.vkvoicenotes.domain.model.VoiceNote
import com.android.artgallery.domain.usecase.base.CompleteUseCase
import com.android.musicplayer.domain.repository.VoiceListRepository
import io.reactivex.Completable
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor( val voiceListRepository: VoiceListRepository) : CompleteUseCase() {

//    fun deleteSongItem(song: VoiceNote) {
//        voiceListRepository.delete(song)
//    }

    private var note: VoiceNote? = null

    fun setNote(note:VoiceNote) {
        this.note = note
    }

    override fun buildUseCaseCompletable(): Completable {
         return voiceListRepository.delete(note)
    }
}