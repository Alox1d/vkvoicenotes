package com.alox1d.vkvoicenotes.recorder.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alox1d.vkvoicenotes.App
import com.alox1d.vkvoicenotes.R
import com.alox1d.vkvoicenotes.internal.util.FileUtils.generateFileNameAAC
import com.alox1d.vkvoicenotes.internal.util.FileUtils.getFile
import com.alox1d.vkvoicenotes.internal.util.FileUtils.renameFile
import com.alox1d.vkvoicenotes.presentation.viewmodel.SingleLiveEvent
import com.alox1d.vkvoicenotes.recorder.model.RecordingAudio
import com.alox1d.vkvoicenotes.recorder.service.RecordingService
import com.alox1d.vkvoicenotes.recorder.service.RecordingService.OnRecordingStatusChangedListener
import java.io.File

/**
 * Manages the connection with RecordingService and the related data.
 */
class RecorderViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private val TAG = RecordingService::class.java.name
        //private const val RECORDING_TIME = 20
        //private const val RECORDING_TIME_MILLS = RECORDING_TIME * 1000
    }

    private val _serviceConnected = SingleLiveEvent<Boolean>()
    val serviceConnected: LiveData<Boolean>
        get() = _serviceConnected

    private val _serviceRecording = MutableLiveData<Boolean>()
    val serviceRecording: LiveData<Boolean>
        get() = _serviceRecording

    private val _toastMsg = SingleLiveEvent<Int>()
    val toastMsg: LiveData<Int>
        get() = _toastMsg


    private val amplitudeLive = MutableLiveData<Int>()
    var timeRemaining = MutableLiveData<String>()
    val secondsElapsed = ObservableInt(0)
    val showPlayBack = ObservableBoolean(false)

    @get:VisibleForTesting
    private var recordService: MutableLiveData<RecordingService?> = MutableLiveData()

    private val _recordingAudio = SingleLiveEvent<RecordingAudio?>()
    val recordingAudio: LiveData<RecordingAudio?>
        get() = _recordingAudio

    @VisibleForTesting
    constructor(application: Application, recordingService: RecordingService?) : this(
        application
    ) {
        this.recordService.value = recordingService
    }

    fun startAndBindRecordingService() {
        val app = getApplication<App>()
        app.bindService(
            RecordingService.makeIntent(getApplication()),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    fun unbindRecordService() {
        if (_serviceConnected.value != true) return
        val app = getApplication<App>()
        app.unbindService(serviceConnection)
//        if (_serviceRecording.value != true) getApplication<Application>().stopService(intent)
//        mRecordService!!.setOnRecordingStatusChangedListener(null)
//        mRecordService = null  //TODO Стоит ли?
        _serviceConnected.value = false
    }

    fun stopRecordingAndService() {
        if (_serviceRecording.value == true)
            recordService.value?.stopRecording()

        unbindRecordService()
    }

    fun getAmplitudeLive(): LiveData<Int> {
        return amplitudeLive
    }

    fun changeRecordingName(newFileName: String) {
        if (newFileName.trim { it <= ' ' }.isNotEmpty()) {
            _recordingAudio.value?.let {

                val oldFile = getFile(it.filePath)
                val newFileNameWithExt = generateFileNameAAC(newFileName)
                val newFilePath = renameFile(it.filePath, it.fileName, newFileNameWithExt, oldFile)

                _recordingAudio.value = it.copy(
                    fileName = newFileNameWithExt,
                    filePath = newFilePath
                )

            }
        }
    }

    fun setServiceRecording(isRecording: Boolean) {
        _serviceRecording.value = (isRecording)
    }

    /**
     * Implementation of ServiceConnection interface.
     * The interaction with the Service is managed by this view model.
     */
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            val binder = service as RecordingService.LocalBinder
            recordService.value = binder.service
            recordService.value?.setOnRecordingStatusChangedListener(onRecordingStatusChangedListener)
            ContextCompat.startForegroundService(
                getApplication(),
                Intent(recordService.value, RecordingService::class.java)
            )
//            ContextCompat.startForegroundService(getApplication(),RecordingService.makeIntent(getApplication()))

            if (_serviceRecording.value != true) {
                recordService.value?.startRecording()
                _serviceRecording.value = true
            }
            _serviceConnected.value = true
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            if (recordService.value != null) {
                recordService.value?.setOnRecordingStatusChangedListener(null)
//                mRecordService = null
            }
            _serviceConnected.value = false
        }
    }

    /**
     * Implementation of RecordingService.OnRecordingStatusChangedListener interface.
     * The Service uses this interface to communicate to the connected component that a
     * recording has started/stopped, and the seconds elapsed, so that the UI can be updated
     * accordingly.
     */
    private val onRecordingStatusChangedListener: OnRecordingStatusChangedListener =
        object : OnRecordingStatusChangedListener {
            override fun onRecordingStarted() {
                _serviceRecording.value = true
                _toastMsg.postValue(R.string.toast_recording_start)
//                timeRemaining.postValue(RECORDING_TIME.toString())
                showPlayBack.set(false)
            }

            override fun onRecordingStopped(
                fileName: String, filePath: String,
                elapsedMillis: Long, createdMillis: Long
            ) {
                _serviceRecording.value = false
                secondsElapsed.set(0)
//                timeRemaining.postValue(getApplication<Application>().getString(R.string.ready))
                _toastMsg.postValue(R.string.toast_recording_saved)

                // Save the recording data in the database.
                _recordingAudio.value =
                    RecordingAudio(fileName, filePath, elapsedMillis, createdMillis)

                showPlayBack.set(true)
            }

            // This method is called from a separate thread.
            override fun onTimerChanged(seconds: Int) {
                secondsElapsed.set(seconds)
//                timeRemaining.postValue((RECORDING_TIME - seconds).toString())
            }

            override fun onAmplitudeInfo(amplitude: Int) {
                amplitudeLive.postValue(amplitude)
            }
        }

}