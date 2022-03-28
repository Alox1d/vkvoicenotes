package com.alox1d.vkvoicenotes.data.repository

import com.alox1d.vkvoicenotes.data.database.AppDatabase
import com.alox1d.vkvoicenotes.data.model.VoiceNote
import com.android.musicplayer.domain.repository.VoiceListRepository

class VoiceListRepositoryImp(private val appDatabase: AppDatabase) : VoiceListRepository {

    override fun delete(note: VoiceNote) {
        appDatabase.voiceNotesDao().delete(note)
    }

    override fun getSongs(): List<VoiceNote>? {
        return appDatabase.voiceNotesDao().loadAll()
    }

    override fun saveSongData(note: VoiceNote):Long {
        return appDatabase.voiceNotesDao().insert(note)
    }
}