package com.android.player.service

import com.android.player.model.AbstractAudio


/**
 * To make an interaction between [SongPlayerService] & [BaseSongPlayerActivity]
 *
 * */
interface OnPlayerServiceCallback {

    fun updateSongData(song: AbstractAudio)

    fun updateSongProgress(duration: Long, position: Long)

    fun setBufferingData(isBuffering: Boolean)

    fun setVisibilityData(isVisibility: Boolean)

    fun setPlayStatus(isPlay: Boolean)

    fun stopService()
}