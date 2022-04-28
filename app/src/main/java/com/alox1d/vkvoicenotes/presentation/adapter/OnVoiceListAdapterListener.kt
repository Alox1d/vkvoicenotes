package com.alox1d.vkvoicenotes.presentation.adapter

import com.alox1d.vkvoicenotes.domain.model.VoiceNote

interface OnVoiceListAdapterListener {

    fun toggleNote(note: VoiceNote, voiceNotes: List<VoiceNote>)

    fun removeNoteItem(note: VoiceNote)
}