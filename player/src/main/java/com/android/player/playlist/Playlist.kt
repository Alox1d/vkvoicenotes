package com.android.player.playlist

import com.android.player.model.AbstractAudio


class Playlist {

    private var list: MutableList<AbstractAudio> = ArrayList()
    private var shuffleList: MutableList<AbstractAudio> = ArrayList()
    var isShuffle = false
    var isRepeat = false
    var isRepeatAll = false

    fun getShuffleOrNormalList(): MutableList<AbstractAudio> {
        return if (isShuffle) shuffleList else list
    }

    fun getCurrentPlaylistSize(): Int = getShuffleOrNormalList().size

    fun setList(list: MutableList<AbstractAudio>): Playlist {
        clearList()
        this.list = list
//        list.shuffle()
        this.shuffleList = ArrayList(list)
        return this
    }

    fun addItems(songList: MutableList<AbstractAudio>) {
        this.list.addAll(songList)
        songList.shuffle()
        this.shuffleList.addAll(songList)
    }

    fun addItem(song: AbstractAudio) {
        this.list.add(song)
        this.shuffleList.add(song)
    }

    fun getItem(index : Int): AbstractAudio?{
        if (index >= getCurrentPlaylistSize()) return null
        return getShuffleOrNormalList()[index]
    }

    private fun clearList() {
        this.list.clear()
        this.shuffleList.clear()
    }


    companion object {

        private val TAG = Playlist::class.java.name
    }
}
