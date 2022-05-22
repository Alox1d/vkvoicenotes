package com.android.musicplayer.domain.usecase

import com.alox1d.vkvoicenotes.domain.model.VoiceNote
import com.alox1d.vkvoicenotes.domain.usecase.base.model.DeleteParams
import com.android.artgallery.domain.usecase.base.CompletableUseCase
import com.android.musicplayer.domain.repository.VoiceListRepository
import io.reactivex.Completable
import java.lang.Exception
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(val voiceListRepository: VoiceListRepository) :
    CompletableUseCase<DeleteParams>() {

//    private var note: VoiceNote? = null
//
//    fun setNote(note: VoiceNote) {
//        this.note = note
//    }

    override fun buildUseCaseCompletable(params: DeleteParams): Completable {
        return voiceListRepository.delete(params.voiceNote)

//        note?.let { return voiceListRepository.delete(it) }
//        return Completable.error(IllegalArgumentException("Voice note id's not set"))
    }

}