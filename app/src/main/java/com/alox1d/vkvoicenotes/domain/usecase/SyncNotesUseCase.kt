package com.android.musicplayer.domain.usecase

import com.alox1d.vkvoicenotes.domain.model.VoiceNote
import com.android.artgallery.domain.usecase.base.CompletableUseCase
import com.android.musicplayer.domain.repository.VoiceListRepository
import io.reactivex.Completable
import javax.inject.Inject

class SyncNotesUseCase @Inject constructor(val voiceListRepository: VoiceListRepository):
    CompletableUseCase() {

    private var notes: List<VoiceNote> = emptyList()

    fun addNotes(notes:List<VoiceNote>) {
        this.notes = notes
    }

    override fun buildUseCaseCompletable(): Completable {
        return voiceListRepository.syncVoicesNotes(notes = this.notes)

    }
}