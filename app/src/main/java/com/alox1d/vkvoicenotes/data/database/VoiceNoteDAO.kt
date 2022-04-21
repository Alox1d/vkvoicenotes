package com.alox1d.vkvoicenotes.data.database

import androidx.room.*
import com.alox1d.vkvoicenotes.data.model.VoiceNoteDTO
import com.alox1d.vkvoicenotes.domain.model.VoiceNote


@Dao
interface VoiceNoteDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: VoiceNoteDTO): Long

    @Query("SELECT * FROM VoiceNoteDTO")
    fun loadAll(): MutableList<VoiceNoteDTO>

    @Delete
    fun delete(note: VoiceNoteDTO)

    @Query("DELETE FROM VoiceNoteDTO")
    fun deleteAll()

    @Query("SELECT * FROM VoiceNoteDTO where id = :noteId")
    fun loadOneBynoteId(noteId: Long): VoiceNoteDTO?

    @Query("SELECT * FROM VoiceNoteDTO where name = :name")
    fun loadOneByNoteName(name: String): VoiceNoteDTO?

    @Update
    fun update(note: VoiceNoteDTO)

}