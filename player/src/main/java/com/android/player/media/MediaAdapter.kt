package com.android.player.media

import android.util.Log
import com.android.player.exo.OnExoPlayerManagerCallback
import com.android.player.model.AbstractAudio
import com.android.player.playlist.PlaylistManager
import java.util.*


/**
 * This class is used to interact with [ExoPlayerManager] & [PlaylistManager]
 *
 * */
class MediaAdapter(
    private val onExoPlayerManagerCallback: OnExoPlayerManagerCallback,
    private val mediaAdapterCallback: OnMediaAdapterCallback
) : OnExoPlayerManagerCallback.OnSongStateCallback, PlaylistManager.OnSongUpdateListener {

    private var playlistManager: PlaylistManager? = null

    init {
        onExoPlayerManagerCallback.setCallback(this)
        playlistManager = PlaylistManager(this)
    }

    fun play(song: AbstractAudio) {
        onExoPlayerManagerCallback.play(song)
    }

    fun play(songList: List<AbstractAudio>, song: AbstractAudio) {
        playlistManager?.setCurrentPlaylist(songList, song)
    }

    fun pause() {
        onExoPlayerManagerCallback.pause()
    }

    fun seekTo(position: Long) {
        onExoPlayerManagerCallback.seekTo(position)
    }

    fun stop() {
        onExoPlayerManagerCallback.stop()
    }

    fun skipToNext() {
        playlistManager?.skipPosition(1)
    }

    fun skipToPrevious() {
        playlistManager?.skipPosition(-1)
    }

    fun addToCurrentPlaylist(songList: MutableList<AbstractAudio>) {
        Log.d(TAG, "addToCurrentPlaylist() called with: songList = $songList")
        playlistManager?.addToPlaylist(songList)
    }

    fun addToCurrentPlaylist(song: AbstractAudio) {
        Log.d(TAG, "addToCurrentPlaylist() called with: song = $song")
        playlistManager?.addToPlaylist(song)
    }

    override fun shuffle(isShuffle: Boolean) {
        playlistManager?.setShuffle(isShuffle)
    }

    override fun repeatAll(isRepeatAll: Boolean) {
        playlistManager?.setRepeatAll(isRepeatAll)
    }

    override fun repeat(isRepeat: Boolean) {
        playlistManager?.setRepeat(isRepeat)
    }


    override fun onSongChanged(song: AbstractAudio) {
        play(song)
        mediaAdapterCallback.onSongChanged(song)
    }

    override fun onSongRetrieveError() {
        //Log.d(TAG, "onSongRetrieveError() called")
    }

    override fun onPlaybackStatusChanged(state: Int) {
        mediaAdapterCallback.onPlaybackStateChanged(state)
    }

    override fun getCurrentSongList(): ArrayList<AbstractAudio>?{
        return playlistManager?.getCurrentSongList()
    }

    override fun getCurrentSong(): AbstractAudio? {
        return playlistManager?.getCurrentSong()
    }

    override fun setCurrentPosition(position: Long, duration: Long) {
        mediaAdapterCallback.setDuration(duration, position)
    }


    override fun onCompletion() {
        if (playlistManager?.isRepeat() == true) {
            onExoPlayerManagerCallback.stop()
            playlistManager?.repeat()
            return
        }
        // Option: if next should be played
//        if (playlistManager?.hasNext() == true) {
//            playlistManager?.skipPosition(1)
//            return
//        }

        if (playlistManager?.isRepeatAll() == true) {
            playlistManager?.skipPosition(-1)
            return
        }

        onExoPlayerManagerCallback.stop()
    }


    companion object {
        private val TAG = MediaAdapter::class.java.name
    }

}