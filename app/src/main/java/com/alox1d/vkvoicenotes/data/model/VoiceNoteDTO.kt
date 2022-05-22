package com.alox1d.vkvoicenotes.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Suppress("DIFFERENT_NAMES_FOR_THE_SAME_PARAMETER_IN_SUPERTYPES")
@Entity
@Parcelize
data class VoiceNoteDTO(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val name: String?,
    val path: String,
    val duration: String?,
    val type:Int=0,
    val date:Long,
    var isPlaying:Boolean = false,
    ) : Parcelable

