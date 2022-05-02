package com.android.musicplayer.domain.usecase

import com.alox1d.vkvoicenotes.domain.model.VoiceNote
import com.alox1d.vkvoicenotes.domain.usecase.base.model.SaveParams
import com.android.artgallery.domain.usecase.base.MaybeUseCase
import com.android.artgallery.domain.usecase.base.SingleUseCase
import com.android.musicplayer.domain.repository.VoiceListRepository
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

class SaveNoteDataUseCase @Inject constructor(val voiceListRepository: VoiceListRepository):
    MaybeUseCase<Long, SaveParams>() {

//    private var note: VoiceNote? = null
//
//    fun setNote(note:VoiceNote) {
//        this.note = note
//    }

    override fun buildUseCaseMaybe(params: SaveParams): Maybe<Long> {
        return voiceListRepository.saveVoiceNotes(params.voiceNote)

//        note?.let {
//            return voiceListRepository.saveVoiceNotes(it)
//        }
//        return Maybe.empty()
    }
}