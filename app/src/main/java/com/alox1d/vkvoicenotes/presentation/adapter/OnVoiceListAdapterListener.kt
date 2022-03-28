package com.alox1d.vkvoicenotes.presentation.adapter

import com.alox1d.vkvoicenotes.data.model.VoiceNote

interface OnVoiceListAdapterListener {

    fun playNote(note: VoiceNote, voiceNotes: ArrayList<VoiceNote>)

    fun removeNoteItem(note: VoiceNote)
}