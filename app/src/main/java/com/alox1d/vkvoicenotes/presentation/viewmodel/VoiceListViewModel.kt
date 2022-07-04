package com.alox1d.vkvoicenotes.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alox1d.vkvoicenotes.domain.model.VoiceNote
import com.alox1d.vkvoicenotes.domain.usecase.base.model.DeleteParams
import com.alox1d.vkvoicenotes.domain.usecase.base.model.SaveParams
import com.alox1d.vkvoicenotes.domain.usecase.base.model.SyncParams
import com.alox1d.vkvoicenotes.domain.usecase.base.model.UpdateParams
import com.alox1d.vkvoicenotes.internal.util.FileUtils
import com.alox1d.vkvoicenotes.recorder.model.RecordingAudio
import com.android.musicplayer.domain.usecase.*
import javax.inject.Inject

class VoiceListViewModel : ViewModel(

) {
    companion object {
        val TAG = VoiceListViewModel::class.java.name
    }

    @Inject
    lateinit var saveAudioDataUseCase: SaveNoteDataUseCase

    @Inject
    lateinit var updateAudioDataUseCase: UpdateNoteDataUseCase

    @Inject
    lateinit var getVoicesUseCase: GetNotesUseCase

    @Inject
    lateinit var deleteNoteUseCase: DeleteNoteUseCase

    @Inject
    lateinit var syncNotesUseCase: SyncNotesUseCase

    private val _playingState = MutableLiveData<PlayingState>()
    val playingState: LiveData<PlayingState>
        get() {
            //TODO Где лучше выполнять сортировку? Здесь не работает
            val state = _playingState
            state.value?.playlist?.apply { sortedByDescending { it.name } }?.let {
                _playingState.value = state.value?.copy(playlist = it)
            }
            return _playingState
        }

    private val _onRecording: MutableLiveData<Boolean> = MutableLiveData()
    val onRecording: LiveData<Boolean>
        get() = _onRecording

    private val _isSyncSuccess: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val isSyncSuccess: LiveData<Boolean>
        get() = _isSyncSuccess
    private val _isSyncError: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val isSyncError: LiveData<Boolean>
        get() = _isSyncError

    private val _savingNote: SingleLiveEvent<VoiceNote?> = SingleLiveEvent()
    val savingNote: LiveData<VoiceNote?>
        get() = _savingNote

    fun toggleNote(note: VoiceNote) {
        val list = _playingState.value?.playlist?.map {
            val isPlaying = if (it == note) !it.isPlaying else false
            it.copy(isPlaying = isPlaying)
        } ?: emptyList()
        note.isPlaying = true
        _playingState.postValue(PlayingState(list, note))
    }

    fun setNotePlayStatus(isPlay: Boolean) {
        if (!isPlay) {
            val state = playingState.value
            state?.let {
                val note = it.playingNote?.apply { isPlaying = isPlay }
                val list = it.playlist.apply {
                    find {
                        it.id == note?.id
                    }?.isPlaying = false
                }
                _playingState.postValue(PlayingState(list, note))
            }
        }
    }

    fun saveVoiceData(recordingAudio: RecordingAudio) {
        var note = _savingNote.value
        if (note == null || note.date != recordingAudio.createdMillis){
            note = VoiceNote(
                name = recordingAudio.fileName,
                path = recordingAudio.filePath,
                duration = recordingAudio.elapsedMillis.toString(),
                date = recordingAudio.createdMillis
            )
        } else {
            note = note.copy(name = recordingAudio.fileName, path = recordingAudio.filePath)
        }

        saveAudioDataUseCase.execute(
            onSuccess = { id ->
                _savingNote.value = note.copy(id = id)
            },
            onComplete = {},
            onError = {},
            params = SaveParams(note)
        )
    }

    fun updateVoiceData(newFileName: String) {
        _savingNote.value?.let {
            if (it.name != null && newFileName.isNotBlank()){
                val oldFile = FileUtils.getFile(it.path)
                val newFileNameWithExt = FileUtils.generateFileNameAAC(newFileName)
                val newFilePath = FileUtils.renameFile(it.path, it.name, newFileNameWithExt, oldFile)
                val note = it.copy(name = newFileName, path = newFilePath)

                updateAudioDataUseCase.execute({}, {}, {}, params = UpdateParams(note))
            }
        }
    }

    fun getVoiceNotesFromDB() {
        getVoicesUseCase.execute(
            onNext = {
                _playingState.postValue(PlayingState(it))
            },
            onError = {
                it.printStackTrace()
            }
        )
    }

    fun removeItemFromList(note: VoiceNote) {
        deleteNoteUseCase.execute({}, {}, DeleteParams(note))
//        val list = _playingState.value?.playlist as? MutableList<VoiceNote> ?: mutableListOf()
//        list.remove(note)
//        _playingState.value = PlayingState(list)
    }

    override fun onCleared() {
        super.onCleared()
        getVoicesUseCase.dispose()
    }

    fun onToggleRecord() {
        _onRecording.value = _onRecording.value != true
    }

//    fun onRecordingNameSet() {
//        _isNameSet.value = _isNameSet.value != true
//    }

    fun syncVK() {
        playingState.value?.playlist?.let {
            syncNotesUseCase.execute(
                onComplete = {
                    _isSyncSuccess.postValue(true) // Как лучше оформить данные отображения успеха/ошибки в UI? Sealed-классами?
                },
                onError = {
                    Log.d(TAG, it.message, it)
                    _isSyncError.postValue(true)
//                _isSyncError.postValue(false)
                },
                params = SyncParams(it)
            )
        }
    }

    fun setRecordState(recording: Boolean) {
        _onRecording.value = recording
    }

    fun setSyncError(isSync: Boolean) {
        _isSyncError.postValue(isSync)
    }
}