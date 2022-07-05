package com.android.musicplayer.domain.usecase

import com.alox1d.vkvoicenotes.domain.usecase.base.model.UpdateParams
import com.android.artgallery.domain.usecase.base.MaybeUseCase
import com.android.musicplayer.domain.repository.VoiceListRepository
import io.reactivex.Maybe
import javax.inject.Inject

class UpdateNoteDataUseCase @Inject constructor(val voiceListRepository: VoiceListRepository) :
    MaybeUseCase<Int, UpdateParams>() {

    override fun buildUseCaseMaybe(params: UpdateParams): Maybe<Int> {
        return voiceListRepository.updateVoiceNote(params.voiceNote)
    }

}