package com.alox1d.vkvoicenotes.domain.model

import android.net.Uri
import android.os.Parcelable
import android.speech.tts.Voice
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
    var uri:Uri? = null
    ) :  AbstractAudio(audioId = id,
    title = name,
    source = path,
    audioType = type,
    length = duration,
    timestampCreated = date),
    Parcelable

