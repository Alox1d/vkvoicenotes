package com.alox1d.vkvoicenotes.recorder.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.alox1d.vkvoicenotes.R
import com.alox1d.vkvoicenotes.internal.util.PathUtils.getDirectoryPath
import com.alox1d.vkvoicenotes.recorder.notification.RecorderNotificationManager
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Service used to record audio. This class implements an hybrid Service (bound and started
 * Service).
 * Compared with the original Service, this class adds a new feature to
 * bound and connect Service to an Activity
 */
class RecordingService : Service() {

    companion object {

        private val TAG = RecordingService::class.java.name

        var onCreateCalls = 0
        var onDestroyCalls = 0
        var onStartCommandCalls = 0

        fun makeIntent(context: Context): Intent {
            return Intent(context.applicationContext, RecordingService::class.java)
        }

    }

    private var mFileName: String = ""
    private var mFilePath: String = ""

    private var mRecorder: MediaRecorder? = null
    private var mStartingTimeMillis: Long = 0
    private var mElapsedMillis: Long = 0
    private var mIncrementTimerTask: TimerTask? = null

    private val binder: IBinder = LocalBinder()
    private var mNotificationManager: RecorderNotificationManager? = null

    private var onRecordingStatusChangedListener: OnRecordingStatusChangedListener? = null


    /**
     * The following code implements a bound Service used to connect this Service to an Activity.
     */
    inner class LocalBinder : Binder() {
        val service: RecordingService
            get() = this@RecordingService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "ServiceOnUnBind")
        //return super.onUnbind(intent);
        return true
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    /**
     * Interface used to communicate to a connected component changes in the status of a
     * recording:
     * - recording started
     * - recording stopped (with file path)
     * - seconds elapsed and max amplitude (useful for graphical effects)
     */
    interface OnRecordingStatusChangedListener {
        fun onRecordingStarted()
        fun onTimerChanged(seconds: Int)
        fun onAmplitudeInfo(amplitude: Int)
        fun onRecordingStopped(
            fileName: String,
            filePath: String,
            elapsedMillis: Long,
            createdMillis: Long
        )
    }

    fun setOnRecordingStatusChangedListener(onRecordingStatusChangedListener: OnRecordingStatusChangedListener?) {
        this.onRecordingStatusChangedListener = onRecordingStatusChangedListener
    }

    /**
     * The following code implements a started Service.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        onStartCommandCalls++
        Log.d(
            TAG,
            "onStartCommand() called with: intent = $intent, flags = $flags, startId = $startId"
        )
        return START_NOT_STICKY
    }

    /**
     * The following code is shared by both started and bound Service.
     */
    override fun onCreate() {
        onCreateCalls++
        super.onCreate()
        mNotificationManager = RecorderNotificationManager(this)
        mNotificationManager?.createMediaNotification()
    }

    override fun onDestroy() {
        onDestroyCalls++
        super.onDestroy()
        if (mRecorder != null) {
            stopRecording()
        }
        if (onRecordingStatusChangedListener != null) onRecordingStatusChangedListener = null
    }

    fun startRecording(duration: Int = 0) {
        setFileNameAndPath()
        createFile(mFilePath)
        mRecorder = MediaRecorder()
        mRecorder?.let {
            it.setAudioSource(MediaRecorder.AudioSource.MIC)
            it.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            it.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            it.setOutputFile(mFilePath)
            it.setMaxDuration(duration) // set the max duration, after which the Service is stopped
            it.setAudioChannels(1)
            it.setAudioSamplingRate(44100)
            it.setAudioEncodingBitRate(192000)

            // Called only if a max duration has been set.
            it.setOnInfoListener { mediaRecorder: MediaRecorder?, what: Int, extra: Int ->
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    stopRecording()
                }
            }
            try {
                it.prepare()
                it.start()
                mStartingTimeMillis = System.currentTimeMillis()
                //isRecording = true
                startTimer()
            } catch (e: IOException) {
                Log.e(TAG, "$TAG - startRecording(): prepare() failed$e")
            }
            if (onRecordingStatusChangedListener != null) {
                onRecordingStatusChangedListener!!.onRecordingStarted()
            }
        }
    }

    private fun setFileNameAndPath() {
        val ext = ".aac"
        mFileName =
            getString(R.string.default_recording_file_prefix) +
                    " " + SimpleDateFormat("dd.MM.yy HH:mm:ss").format(
                System.currentTimeMillis()
            ) + ext

        mFilePath = getDirectoryPath(this) + "/" + mFileName
        //mDirectoryPath = getDirectoryPath(this) + "/"
        Log.d(TAG, "mFilePath =  $mFilePath")
    }

    private fun createFile(filePath: String) {
        val file = File(filePath)
        Log.d(TAG, "createdFileAbsolutePath =  ${file.absolutePath}")
    }

    private fun startTimer() {
        val mTimer = Timer()

        // Increment seconds.
        mElapsedMillis = 0
        mIncrementTimerTask = object : TimerTask() {
            override fun run() {
                mElapsedMillis += 100
                if (onRecordingStatusChangedListener != null) {
                    onRecordingStatusChangedListener!!.onTimerChanged(mElapsedMillis.toInt() / 1000)
                }
                if (onRecordingStatusChangedListener != null && mRecorder != null) {
                    try {
                        mRecorder?.let {
                            onRecordingStatusChangedListener!!.onAmplitudeInfo(it.maxAmplitude)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        mTimer.scheduleAtFixedRate(mIncrementTimerTask, 0, 100)
    }

    fun stopRecording() {
        mRecorder?.stop()
        val mElapsedMillis = System.currentTimeMillis() - mStartingTimeMillis
        mRecorder?.release()
        //isRecording = false
        mRecorder = null

        // Communicate the file path to the connected Activity.
        if (onRecordingStatusChangedListener != null) {
            onRecordingStatusChangedListener!!.onRecordingStopped(
                mFileName,
                mFilePath,
                mElapsedMillis,
                mStartingTimeMillis
            )
        }

        // Stop timer.
        if (mIncrementTimerTask != null) {
            mIncrementTimerTask!!.cancel()
            mIncrementTimerTask = null
        }
        if (onRecordingStatusChangedListener == null) stopSelf()
        stopForeground(true)
        mNotificationManager = null
        stopSelf()
    }

}