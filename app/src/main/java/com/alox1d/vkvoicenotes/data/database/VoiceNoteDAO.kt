package com.alox1d.vkvoicenotes.data.database

import androidx.room.*
import com.alox1d.vkvoicenotes.data.model.VoiceNoteDTO
import com.alox1d.vkvoicenotes.domain.model.VoiceNote
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single


@Dao
interface VoiceNoteDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: VoiceNoteDTO?): Single<Long>

    @Query("SELECT * FROM VoiceNoteDTO")
    fun loadAll(): Flowable<List<VoiceNoteDTO>>

    @Delete
    fun delete(note: VoiceNoteDTO?): Completable

    @Query("DELETE FROM VoiceNoteDTO")
    fun deleteAll():Completable

    @Query("SELECT * FROM VoiceNoteDTO where id = :noteId")
    fun loadOneBynoteId(noteId: Long): Single<VoiceNoteDTO?>

    @Query("SELECT * FROM VoiceNoteDTO where name = :name")
    fun loadOneByNoteName(name: String): Single<VoiceNoteDTO?>

    @Update
    fun update(note: VoiceNoteDTO): Completable

}