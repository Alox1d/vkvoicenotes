package com.alox1d.vkvoicenotes.presentation.viewmodel

import com.alox1d.vkvoicenotes.domain.model.VoiceNote

class PlayingState(
    val playlist: List<VoiceNote>,
    val playingNote: VoiceNote? = null
)