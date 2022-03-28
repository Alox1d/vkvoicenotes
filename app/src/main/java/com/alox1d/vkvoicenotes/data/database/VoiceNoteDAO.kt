package com.alox1d.vkvoicenotes.data.database

import androidx.room.*
import com.alox1d.vkvoicenotes.data.model.VoiceNote


@Dao
interface VoiceNoteDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: VoiceNote): Long

    @Query("SELECT * FROM VoiceNote")
    fun loadAll(): MutableList<VoiceNote>

    @Delete
    fun delete(note: VoiceNote)

    @Query("DELETE FROM VoiceNote")
    fun deleteAll()

    @Query("SELECT * FROM VoiceNote where id = :noteId")
    fun loadOneBynoteId(noteId: Long): VoiceNote?

    @Query("SELECT * FROM VoiceNote where title = :name")
    fun loadOneByNoteName(name: String): VoiceNote?

    @Update
    fun update(note: VoiceNote)

}