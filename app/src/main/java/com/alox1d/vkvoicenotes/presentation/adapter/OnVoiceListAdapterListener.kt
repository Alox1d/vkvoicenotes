package com.alox1d.vkvoicenotes.presentation.adapter

import com.alox1d.vkvoicenotes.domain.model.VoiceNote

interface OnVoiceListAdapterListener {

    fun toggleNote(note: VoiceNote, voiceNotes: ArrayList<VoiceNote>)

    fun removeNoteItem(note: VoiceNote)
}