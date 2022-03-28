package com.android.player.media


import com.android.player.model.AVoiceNote
import java.util.ArrayList

/**
 * To return the result of [MediaAdapter]
 *
 * and also to make an interaction between [PlayerService] & [MediaAdapter]
 *
 * */
interface OnMediaAdapterCallback {

    fun onSongChanged(song : AVoiceNote)

    fun onPlaybackStateChanged(state : Int)

    fun setDuration(duration: Long, position: Long)

    fun addNewPlaylistToCurrent(songList: ArrayList<AVoiceNote>)

    fun onShuffle(isShuffle: Boolean)

    fun onRepeat(isRepeat: Boolean)

    fun onRepeatAll(repeatAll: Boolean)

}