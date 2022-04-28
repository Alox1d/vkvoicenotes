package com.alox1d.vkvoicenotes.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alox1d.vkvoicenotes.domain.model.VoiceNote
import com.android.musicplayer.domain.usecase.DeleteNoteUseCase
import com.android.musicplayer.domain.usecase.GetNotesUseCase
import com.android.musicplayer.domain.usecase.SaveNoteDataUseCase
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class VoiceListViewModel(

) : ViewModel(
) {
    @Inject
    lateinit var saveAudioDataUseCase: SaveNoteDataUseCase

    @Inject
    lateinit var getAudiosUseCase: GetNotesUseCase

    @Inject
    lateinit var deleteNoteUseCase: DeleteNoteUseCase

    private val compositeDisposable by lazy { CompositeDisposable() }

    private val _playingState = MutableLiveData<PlayingState>()
    val playingState: LiveData<PlayingState>
        get() = _playingState

    private val _recording = MutableLiveData(false)
    val recording: LiveData<Boolean>
        get() = _recording
    private val _isNameSet = MutableLiveData(false)
    val isNameSet: LiveData<Boolean>
        get() = _isNameSet

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
                it.playingNote?.isPlaying = isPlay
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

        saveAudioDataUseCase.saveNoteItem(note)
        val list: MutableList<VoiceNote> =
            _playingState.value?.playlist?.toMutableList() ?: mutableListOf()
        list.add(note)
        _playingState.value = PlayingState(list)
    }

    fun getVoiceNotesFromDB() {
        _playingState.value = PlayingState(getAudiosUseCase.getAudios() ?: emptyList())
    }

    fun removeItemFromList(note: VoiceNote) {
        deleteNoteUseCase.deleteSongItem(note)
        val list = _playingState.value?.playlist as? MutableList<VoiceNote> ?: mutableListOf()
        list.remove(note)
        _playingState.value = PlayingState(list)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun onToggleRecord() {
        _recording.value = _recording.value != true
    }

    fun onNameSet() {
        _isNameSet.value = _isNameSet.value != true

    }
}