package com.alox1d.vkvoicenotes.data.repository

import com.alox1d.vkvoicenotes.data.database.AppDatabase
import com.alox1d.vkvoicenotes.data.model.mapToDTO
import com.alox1d.vkvoicenotes.data.model.mapToDomain
import com.alox1d.vkvoicenotes.domain.model.VoiceNote
import com.android.musicplayer.domain.repository.VoiceListRepository

class VoiceListRepositoryImp(private val appDatabase: AppDatabase) : VoiceListRepository {

    override fun delete(voiceNote: VoiceNote) {
        appDatabase.voiceNotesDao().delete(voiceNote.mapToDTO())
    }

    override fun getSongs(): List<VoiceNote>? {
        return appDatabase.voiceNotesDao().loadAll().map {
            it.mapToDomain()
        }
    }

    override fun saveSongData(voiceNote: VoiceNote):Long {
        return appDatabase.voiceNotesDao().insert(voiceNote.mapToDTO())
    }
}