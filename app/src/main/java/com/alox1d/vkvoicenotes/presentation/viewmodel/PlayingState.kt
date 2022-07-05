package com.alox1d.vkvoicenotes.presentation.viewmodel

import com.alox1d.vkvoicenotes.domain.model.VoiceNote

data class PlayingState(
    val playlist: List<VoiceNote>,
    val playingNote: VoiceNote? = null
)