package com.alox1d.vkvoicenotes.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alox1d.vkvoicenotes.data.model.VoiceNote
import com.android.musicplayer.domain.usecase.DeleteNoteUseCase
import com.android.musicplayer.domain.usecase.GetNotesUseCase
import com.android.musicplayer.domain.usecase.SaveNoteDataUseCase
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class VoiceListViewModel (

): ViewModel(
) {
    @Inject
    lateinit var saveAudioDataUseCase: SaveNoteDataUseCase
    @Inject
    lateinit var getAudiosUseCase: GetNotesUseCase
    @Inject
    lateinit var deleteNoteUseCase: DeleteNoteUseCase
//    @Inject
//    lateinit var repository: TrendingRepository

    private val compositeDisposable by lazy { CompositeDisposable() }

    init {
//        DaggerAppComponent.create().inject(this)
//        compositeDisposable.add(repository.fetchDataFromDatabase())
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
    private val _onPlayStart = MutableLiveData<Boolean>()
    val onPlayStart: LiveData<Boolean>
        get() = _onPlayStart
    fun onPlayClicked() {
        _onPlayStart.value?.let { _onPlayStart.value = !it }
    }


    val playlistData = MutableLiveData<List<VoiceNote>>()
    var recording = MutableLiveData(false)
    var isNameSet = MutableLiveData(false)

    fun saveSongData(note: VoiceNote) {
        saveAudioDataUseCase.saveNoteItem(note)
        val list = playlistData.value as ArrayList<VoiceNote>
        list.add(note)
        playlistData.value = list
    }

    fun getVoiceNotesFromDB() {
        playlistData.value = getAudiosUseCase.getAudios()
    }

    fun removeItemFromList(note: VoiceNote) {
            deleteNoteUseCase.deleteSongItem(note)
            val list = playlistData.value as ArrayList<VoiceNote>
            list.remove(note)
            playlistData.value = list
    }
}