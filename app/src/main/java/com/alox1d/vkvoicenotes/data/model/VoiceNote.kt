package com.alox1d.vkvoicenotes.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.player.model.AVoiceNote
import kotlinx.android.parcel.Parcelize

@Suppress("DIFFERENT_NAMES_FOR_THE_SAME_PARAMETER_IN_SUPERTYPES")
@Entity
@Parcelize
data class VoiceNote(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String?,
    var path: String,
    var duration: String?,
    var type:Int=0,
    var date:Long,
    ) : AVoiceNote(id, name, path, type, duration, timestampCreated = date), Parcelable