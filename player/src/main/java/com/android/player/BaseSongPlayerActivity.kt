package com.android.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.player.AudioPlayerViewModel.Companion.getPlayerViewModelInstance
import com.android.player.model.AbstractAudio
import com.android.player.service.OnPlayerServiceCallback
import com.android.player.service.SongPlayerService


open class BaseSongPlayerActivity : AppCompatActivity(), OnPlayerServiceCallback {


    private var mPlayService: SongPlayerService? = null
    private var mServiceIntent: Intent? = null
    private var mBound = false
    private var mSong: AbstractAudio? = null
    private var mSongList: List<AbstractAudio>? = null
    private var msg = 0
    val audioPlayerViewModel: AudioPlayerViewModel = getPlayerViewModelInstance()


    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                ACTION_PLAY_SONG_IN_LIST -> mPlayService?.play(mSongList, mSong)
                ACTION_PAUSE -> mPlayService?.pause()
                ACTION_STOP -> {
                    mPlayService?.stop()
                    audioPlayerViewModel.stop()
                }
            }
        }
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to SongPlayerService, cast the IBinder and get SongPlayerService instance
            val binder = service as SongPlayerService.LocalBinder
            mPlayService = binder.service
            mBound = true
            mPlayService?.subscribeToSongPlayerUpdates()
            mHandler.sendEmptyMessage(msg)
            mPlayService?.setOnPlayStatusChangedListener(this@BaseSongPlayerActivity)
        }

        override fun onServiceDisconnected(classname: ComponentName) {
            mBound = false
            mPlayService?.removeListener()
            mPlayService = null
        }
    }

    private fun bindPlayerService() {
        if (!mBound) {
            mServiceIntent = Intent(this, SongPlayerService::class.java)
            bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE)
        }
    }


    fun play(songList: List<AbstractAudio>?, song: AbstractAudio) {
        msg = ACTION_PLAY_SONG_IN_LIST
        mSong = song
        mSongList = songList
//        audioPlayerViewModel.setPlayStatus(true)

//        if (mService == null) else {
//            audioPlayerViewModel.setPlayStatus(false)
//        }

        if (mPlayService == null) bindPlayerService()
        else {
            mHandler.sendEmptyMessage(msg)
            Log.d(TAG,"thread ${Thread.currentThread().name}")
        }
    }

    private fun pause() {
        msg = ACTION_PAUSE
        audioPlayerViewModel.setPlayStatus(false)
        if (mPlayService == null) bindPlayerService()
        else mHandler.sendEmptyMessage(msg)
    }

    fun stop() {
        msg = ACTION_STOP
        audioPlayerViewModel.setPlayStatus(false)
        if (mPlayService == null) bindPlayerService()
        else mHandler.sendEmptyMessage(msg)
    }

    fun next() {
        mPlayService?.skipToNext()
    }

    fun previous() {
        mPlayService?.skipToPrevious()
    }

    fun toggle(
        playlist: List<AbstractAudio>,
        audio: AbstractAudio
    ) {
        if (audio == mSong && audioPlayerViewModel.isPlayData.value == true) pause()
        else play(playlist,audio)
//            audioPlayerViewModel.playerData.value?.let { it1 -> play(playlist, it1) }
    }

    fun seekTo(position: Long?) {
        position?.let { nonNullPosition ->
            audioPlayerViewModel.seekTo(nonNullPosition)
            mPlayService?.seekTo(nonNullPosition)
        }
    }

    fun addNewPlaylistToCurrent(songList: MutableList<AbstractAudio>) {
        mPlayService?.addNewPlaylistToCurrent(songList)
    }

    fun shuffle() {
        mPlayService?.onShuffle(audioPlayerViewModel.isShuffleData.value ?: false)
        audioPlayerViewModel.shuffle()
    }

    fun repeatAll() {
        mPlayService?.onRepeatAll(audioPlayerViewModel.isRepeatAllData.value ?: false)
        audioPlayerViewModel.repeatAll()
    }

    fun repeat() {
        mPlayService?.onRepeat(audioPlayerViewModel.isRepeatData.value ?: false)
        audioPlayerViewModel.repeat()
    }

    override fun updateSongData(song: AbstractAudio) {
        audioPlayerViewModel.updateSong(song)
    }

    override fun setPlayStatus(isPlay: Boolean) {
        audioPlayerViewModel.setPlayStatus(isPlay)
    }

    override fun updateSongProgress(duration: Long, position: Long) {
        audioPlayerViewModel.setChangePosition(position, duration)
    }

    override fun setBufferingData(isBuffering: Boolean) {
        audioPlayerViewModel.setBuffering(isBuffering)
    }

    override fun setVisibilityData(isVisibility: Boolean) {
        audioPlayerViewModel.setVisibility(isVisibility)
    }

    private fun unbindService(){
        if (mBound) {
            unbindService(mConnection)
            mBound = false
        }
    }

    override fun stopService(){
        unbindService()
        mPlayService = null
    }

    override fun onDestroy() {
        stopService()
        super.onDestroy()
    }


    companion object {

        private val TAG = BaseSongPlayerActivity::class.java.name
        const val AUDIO_LIST_KEY = "SONG_LIST_KEY"
        private const val ACTION_PLAY_SONG_IN_LIST = 1
        private const val ACTION_PAUSE = 2
        private const val ACTION_STOP = 3
    }
}