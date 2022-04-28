package com.android.musicplayer.domain.usecase

import android.net.Uri
import com.alox1d.vkvoicenotes.data.model.VoiceNoteDTO
import com.alox1d.vkvoicenotes.domain.model.VoiceNote
import com.android.artgallery.domain.usecase.base.CompleteUseCase
import com.android.artgallery.domain.usecase.base.SingleUseCase
import com.android.musicplayer.domain.repository.VoiceListRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class SyncNotesUseCase @Inject constructor(val voiceListRepository: VoiceListRepository):
    CompleteUseCase() {

    private var notes: List<VoiceNote> = emptyList()

    fun addNotes(notes:List<VoiceNote>) {
        this.notes = notes
    }

    override fun buildUseCaseCompletable(): Completable {
        return voiceListRepository.syncVoicesNotes(notes = this.notes)

    }
}