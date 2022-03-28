package com.android.player.exo

import com.android.player.model.AVoiceNote
import java.util.ArrayList

/**
 * To make an interaction between [ExoPlayerManager] & [MediaController]
 *
 * and to return result from [ExoPlayerManager]
 *
 * */
interface OnExoPlayerManagerCallback {

    fun getCurrentStreamPosition(): Long

    fun stop()

    fun play(aSong: AVoiceNote)

    fun pause()

    fun seekTo(position: Long)

    fun setCallback(callback: OnSongStateCallback)

    /**
     * This class gives the information about current song
     * (position, the state of completion, when it`s changed, ...)
     *
     * */
    interface OnSongStateCallback {

        fun onCompletion()

        fun onPlaybackStatusChanged(state : Int)

        fun setCurrentPosition(position: Long, duration: Long)

        fun getCurrentSong(): AVoiceNote?

        fun getCurrentSongList(): ArrayList<AVoiceNote>?

        fun shuffle(isShuffle: Boolean)

        fun repeat(isRepeat: Boolean)

        fun repeatAll(isRepeatAll: Boolean)

    }

}
