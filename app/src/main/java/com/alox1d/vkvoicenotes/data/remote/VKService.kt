package com.alox1d.vkvoicenotes.data.remote

import android.net.Uri
import com.alox1d.vkvoicenotes.data.model.VoiceNoteDTO
import com.vk.api.sdk.VK

class VKService {
    fun uploadDoc(note:VoiceNoteDTO) {
            VK.executeSync(VKUsersCommand(note))
    }
}