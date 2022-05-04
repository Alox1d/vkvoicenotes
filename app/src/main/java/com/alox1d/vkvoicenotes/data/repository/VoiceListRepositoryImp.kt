package com.alox1d.vkvoicenotes.data.repository

import com.alox1d.vkvoicenotes.data.database.AppDatabase
import com.alox1d.vkvoicenotes.data.model.VoiceNoteMapper
import com.alox1d.vkvoicenotes.data.remote.VKService
import com.alox1d.vkvoicenotes.domain.model.VoiceNote
import com.android.musicplayer.domain.repository.VoiceListRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

class VoiceListRepositoryImp(
    private val appDatabase: AppDatabase,
    private val mapper: VoiceNoteMapper
) : VoiceListRepository {

    override fun delete(voiceNote: VoiceNote): Completable {
        return appDatabase.voiceNotesDao().delete(mapper.mapToDTO(voiceNote))
    }

    override fun getVoiceNotes(): Flowable<List<VoiceNote>> {
        // map вынесен в репозиторий, остальное - в use-case (base)
        return appDatabase.voiceNotesDao().loadAll().map { list -> list.map { mapper.mapToDomain(it) } }
    }

    override fun saveVoiceNotes(note: VoiceNote): Maybe<Long> {
        return appDatabase.voiceNotesDao().insert(mapper.mapToDTO(note))
    }

    override fun updateVoiceNote(note: VoiceNote): Maybe<Int> {
        return appDatabase.voiceNotesDao().update(mapper.mapToDTO(note))
    }

    override fun syncVoicesNotes(notes: List<VoiceNote>):Completable {
        return Completable.fromCallable {
            (notes.map { VKService().uploadNote(mapper.mapToDTO(it)) })
        }

    }
}