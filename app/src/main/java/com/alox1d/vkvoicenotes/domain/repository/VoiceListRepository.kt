package com.android.musicplayer.domain.repository

import com.alox1d.vkvoicenotes.data.model.VoiceNote


interface VoiceListRepository {

    fun saveSongData(note: VoiceNote):Long

    fun getSongs(): List<VoiceNote>?

    fun delete(song: VoiceNote)

}