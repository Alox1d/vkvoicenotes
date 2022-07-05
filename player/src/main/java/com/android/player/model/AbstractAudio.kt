package com.android.player.model

import android.os.Parcelable

abstract class AbstractAudio(
    @Transient
    var audioId: Long,
    var title: String? = "",
    var source: String? = "",
    var audioType: Int = 0,
    var length: String? = "",
    var downloadPath: String? = "",
    var timestampCreated:Long = 0
)
    : Parcelable
{

    @Transient
    var totalDuration: Long = 0
    @Transient
    var currentPosition: Long = 0
    @Transient
    var playingPercent = 0

    private fun calculatePlayingPercent(): Int {
        return if (currentPosition == 0L || totalDuration == 0L) 0 else (currentPosition * 100 / totalDuration).toInt()
    }

}