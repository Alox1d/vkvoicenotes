package com.android.musicplayer.domain.repository

import android.net.Uri
import com.alox1d.vkvoicenotes.data.model.VoiceNoteDTO
import com.alox1d.vkvoicenotes.domain.model.VoiceNote
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single


interface VoiceListRepository {

    fun saveVoiceNotes(note: VoiceNote?):Single<Long>

    fun getVoiceNotes(): Flowable<List<VoiceNote>>

    fun delete(voiceNote: VoiceNote?): Completable

    fun syncVoicesNotes(notes:List<VoiceNote>):Completable

}