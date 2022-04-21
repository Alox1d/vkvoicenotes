package com.android.player.media


import com.android.player.model.AbstractAudio
import java.util.ArrayList

/**
 * To return the result of [MediaAdapter]
 *
 * and also to make an interaction between [PlayerService] & [MediaAdapter]
 *
 * */
interface OnMediaAdapterCallback {

    fun onSongChanged(song : AbstractAudio)

    fun onPlaybackStateChanged(state : Int)

    fun setDuration(duration: Long, position: Long)

    fun addNewPlaylistToCurrent(songList: ArrayList<AbstractAudio>)

    fun onShuffle(isShuffle: Boolean)

    fun onRepeat(isRepeat: Boolean)

    fun onRepeatAll(repeatAll: Boolean)

}