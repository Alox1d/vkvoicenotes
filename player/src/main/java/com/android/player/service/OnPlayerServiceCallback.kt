package com.android.player.service

import com.android.player.model.AVoiceNote


/**
 * To make an interaction between [SongPlayerService] & [BaseSongPlayerActivity]
 *
 * */
interface OnPlayerServiceCallback {

    fun updateSongData(song: AVoiceNote)

    fun updateSongProgress(duration: Long, position: Long)

    fun setBufferingData(isBuffering: Boolean)

    fun setVisibilityData(isVisibility: Boolean)

    fun setPlayStatus(isPlay: Boolean)

    fun stopService()
}