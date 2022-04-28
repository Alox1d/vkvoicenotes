package com.android.musicplayer.domain.usecase

import com.alox1d.vkvoicenotes.domain.model.VoiceNote
import com.android.artgallery.domain.usecase.base.CompleteUseCase
import com.android.artgallery.domain.usecase.base.SingleUseCase
import com.android.musicplayer.domain.repository.VoiceListRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class  SaveNoteDataUseCase @Inject constructor(val voiceListRepository: VoiceListRepository):SingleUseCase<Long>() {

    private var note: VoiceNote? = null

    fun setNote(note:VoiceNote) {
        this.note = note
    }

    override fun buildUseCaseSingle(): Single<Long> {
       return voiceListRepository.saveVoiceNotes(note)
    }
}