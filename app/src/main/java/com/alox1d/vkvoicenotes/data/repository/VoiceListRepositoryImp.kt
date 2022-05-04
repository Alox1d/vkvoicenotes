package com.alox1d.vkvoicenotes.data.repository

import com.alox1d.vkvoicenotes.data.database.AppDatabase
import com.alox1d.vkvoicenotes.data.model.VoiceNoteMapper
import com.alox1d.vkvoicenotes.domain.model.VoiceNote
import com.android.musicplayer.domain.repository.VoiceListRepository

class VoiceListRepositoryImp(
    private val appDatabase: AppDatabase,
    private val mapper: VoiceNoteMapper
) : VoiceListRepository {

    override fun delete(voiceNote: VoiceNote) {
        appDatabase.voiceNotesDao().delete(mapper.mapToDTO(voiceNote))
    }

    override fun getSongs(): List<VoiceNote>? {
        return appDatabase.voiceNotesDao().loadAll().map {
            mapper.mapToDomain(it)
        }
    }

    override fun saveSongData(voiceNote: VoiceNote): Long {
        return appDatabase.voiceNotesDao().insert(mapper.mapToDTO(voiceNote))
    }
}