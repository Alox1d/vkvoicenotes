package com.alox1d.vkvoicenotes.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alox1d.vkvoicenotes.domain.model.VoiceNote
import com.android.musicplayer.domain.usecase.DeleteNoteUseCase
import com.android.musicplayer.domain.usecase.GetNotesUseCase
import com.android.musicplayer.domain.usecase.SaveNoteDataUseCase
import com.android.musicplayer.domain.usecase.SyncNotesUseCase
import javax.inject.Inject

class VoiceListViewModel(

) : ViewModel(
) {
    @Inject
    lateinit var saveAudioDataUseCase: SaveNoteDataUseCase

    @Inject
    lateinit var getVoicesUseCase: GetNotesUseCase

    @Inject
    lateinit var deleteNoteUseCase: DeleteNoteUseCase

    @Inject
    lateinit var syncNotesUseCase: SyncNotesUseCase

    private val _playingState = MutableLiveData<PlayingState>()
    val playingState: LiveData<PlayingState>
        get() = _playingState

    private val _recording = MutableLiveData(false)
    val recording: LiveData<Boolean>
        get() = _recording
    private val _isNameSet = MutableLiveData(false)
    val isNameSet: LiveData<Boolean>
        get() = _isNameSet

    private val _isSyncSuccess = MutableLiveData(false)
    val isSyncSuccess: LiveData<Boolean>
        get() = _isSyncSuccess
    private val _isSyncError = MutableLiveData(false)
    val isSyncError: LiveData<Boolean>
        get() = _isSyncError


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
            val state  = playingState.value
            state?.let {
                val note = it.playingNote?.apply { isPlaying = isPlay }
                val list = it.playlist.apply {
                    find {
                        it.id == note?.id
                    }?.isPlaying = false
                }
                _playingState.postValue(PlayingState(list, note))
            }
//            val list = _playingState.value?.playlist?.map {
//                val isPlaying = if (it == note) !it.isPlaying else false
//                it.copy(isPlaying = isPlaying)
//            } ?: emptyList()
        }
    }

    fun saveVoiceData(note: VoiceNote) {
        _isNameSet.value = _isNameSet.value != true

        saveAudioDataUseCase.setNote(note)
        saveAudioDataUseCase.execute({},{})
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
        deleteNoteUseCase.setNote(note)
        deleteNoteUseCase.execute({},{})
//        val list = _playingState.value?.playlist as? MutableList<VoiceNote> ?: mutableListOf()
//        list.remove(note)
//        _playingState.value = PlayingState(list)
    }

    override fun onCleared() {
        super.onCleared()
        getVoicesUseCase.dispose()
    }

    fun onToggleRecord() {
        _recording.value = _recording.value != true
    }

    fun onNameSet() {
        _isNameSet.value = _isNameSet.value != true

    }

    fun syncVK() {
        playingState.value?.playlist?.let {
            syncNotesUseCase.addNotes(
                it
//                it.map {
//                Uri.parse(it.path)
//        }
//                it.map {
//                    it.uri = Uri.parse(PathUtils.getPath(context, Uri.parse(it.path) ))
//                    it
//                }
        )
            syncNotesUseCase.execute(
                onComplete = {
               _isSyncSuccess.postValue(true) // Как лучше оформить данные отображения успеха/ошибки в UI? Sealed-классами?
            },
                onError = {
                _isSyncError.postValue(true)
                _isSyncError.postValue(false)
            })
        }


    }
}