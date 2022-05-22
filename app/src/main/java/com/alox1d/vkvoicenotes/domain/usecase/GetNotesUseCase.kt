package com.android.musicplayer.domain.usecase

import com.alox1d.vkvoicenotes.domain.model.VoiceNote
import com.android.artgallery.domain.usecase.base.FlowableUseCase
import com.android.artgallery.domain.usecase.base.SingleUseCase
import com.android.musicplayer.domain.repository.VoiceListRepository
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class GetNotesUseCase @Inject constructor( val voiceListRepository: VoiceListRepository):
    FlowableUseCase<List<VoiceNote>>() {

    override fun buildUseCaseFlowable(): Flowable<List<VoiceNote>> {
        return voiceListRepository.getVoiceNotes()
    }
}