package com.alox1d.vkvoicenotes.domain.model

import android.os.Parcelable
import com.alox1d.vkvoicenotes.data.model.VoiceNoteDTO
import com.android.player.model.AbstractAudio
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VoiceNote(
    var id: Int = 0,
    val name: String?,
    val path: String,
    val duration: String?,
    val type:Int=0,
    val date:Long,
    var isPlaying:Boolean = false,
    ) :  AbstractAudio(audioId = id,
    title = name,
    source = path,
    audioType = type,
    length = duration,
    timestampCreated = date),
    Parcelable


