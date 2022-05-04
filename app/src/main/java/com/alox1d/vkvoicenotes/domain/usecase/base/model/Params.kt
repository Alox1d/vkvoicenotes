package com.alox1d.vkvoicenotes.domain.usecase.base.model

import com.alox1d.vkvoicenotes.domain.model.VoiceNote

interface Parameters

class DeleteParams(val voiceNote: VoiceNote) : Parameters
class SyncParams(val voiceNotes: List<VoiceNote>) : Parameters
class SaveParams(val voiceNote: VoiceNote) : Parameters
class UpdateParams(val voiceNote: VoiceNote) : Parameters