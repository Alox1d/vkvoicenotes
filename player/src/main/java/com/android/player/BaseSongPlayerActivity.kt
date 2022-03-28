package com.android.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import com.android.player.AudioPlayerViewModel.Companion.getPlayerViewModelInstance
import com.android.player.model.AVoiceNote
import com.android.player.service.OnPlayerServiceCallback
import com.android.player.service.SongPlayerService


open class BaseSongPlayerActivity : AppCompatActivity(), OnPlayerServiceCallback {


    private var mService: SongPlayerService? = null
    private var mServiceIntent: Intent? = null
    private var mBound = false
    private var mSong: AVoiceNote? = null
    private var mSongList: MutableList<AVoiceNote>? = null
    private var msg = 0
    val audioPlayerViewModel: AudioPlayerViewModel = getPlayerViewModelInstance()


    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                ACTION_PLAY_SONG_IN_LIST -> mService?.play( mSong)
                ACTION_PAUSE -> mService?.pause()
                ACTION_STOP -> {
                    mService?.stop()
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
            mService = binder.service
            mBound = true
            mService?.subscribeToSongPlayerUpdates()
            mHandler.sendEmptyMessage(msg)
            mService?.addListener(this@BaseSongPlayerActivity)
        }

        override fun onServiceDisconnected(classname: ComponentName) {
            mBound = false
            mService?.removeListener()
            mService = null
        }
    }

    private fun bindPlayerService() {
        if (!mBound) {
            mServiceIntent = Intent(this, SongPlayerService::class.java)
            bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE)
        }
        else {
            stopService(mServiceIntent)
            unbindService(mConnection)
            mBound = false
            mService?.removeListener()
            mService = null
            mServiceIntent = Intent(this, SongPlayerService::class.java)
            bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE)
        }
    }


    fun play(songList: MutableList<AVoiceNote>?, song: AVoiceNote) {
        msg = ACTION_PLAY_SONG_IN_LIST
        mSong = song
        mSongList = songList
        if (mService == null)audioPlayerViewModel.setPlayStatus(true) else {
            audioPlayerViewModel.setPlayStatus(false)
        }
        bindPlayerService()
//        else {toggle()}
    }

    private fun pause() {
        msg = ACTION_PAUSE
        audioPlayerViewModel.setPlayStatus(false)
        if (mService == null) bindPlayerService()
        else mHandler.sendEmptyMessage(msg)
    }

    fun stop() {
        msg = ACTION_STOP
        audioPlayerViewModel.setPlayStatus(false)
        if (mService == null) bindPlayerService()
        else mHandler.sendEmptyMessage(msg)
    }

    fun next() {
        mService?.skipToNext()
    }

    fun previous() {
        mService?.skipToPrevious()
    }

    fun toggle() {
        if (audioPlayerViewModel.isPlayData.value == true) pause()
        else audioPlayerViewModel.playerData.value?.let { it1 -> play(mSongList, it1) }
    }

    fun seekTo(position: Long?) {
        position?.let { nonNullPosition ->
            audioPlayerViewModel.seekTo(nonNullPosition)
            mService?.seekTo(nonNullPosition)
        }
    }

    fun addNewPlaylistToCurrent(songList: ArrayList<AVoiceNote>) {
        mService?.addNewPlaylistToCurrent(songList)
    }

    fun shuffle() {
        mService?.onShuffle(audioPlayerViewModel.isShuffleData.value ?: false)
        audioPlayerViewModel.shuffle()
    }

    fun repeatAll() {
        mService?.onRepeatAll(audioPlayerViewModel.isRepeatAllData.value ?: false)
        audioPlayerViewModel.repeatAll()
    }

    fun repeat() {
        mService?.onRepeat(audioPlayerViewModel.isRepeatData.value ?: false)
        audioPlayerViewModel.repeat()
    }

    override fun updateSongData(song: AVoiceNote) {
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
        mService = null
    }

    override fun onDestroy() {
        stopService()
        super.onDestroy()
    }


    companion object {

        private val TAG = BaseSongPlayerActivity::class.java.name
        const val SONG_LIST_KEY = "SONG_LIST_KEY"
        private const val ACTION_PLAY_SONG_IN_LIST = 1
        private const val ACTION_PAUSE = 2
        private const val ACTION_STOP = 3
    }
}