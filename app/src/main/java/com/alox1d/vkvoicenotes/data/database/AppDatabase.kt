package com.alox1d.vkvoicenotes.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alox1d.vkvoicenotes.data.model.VoiceNoteDTO
import com.alox1d.vkvoicenotes.domain.model.VoiceNote
import com.alox1d.vkvoicenotes.internal.DATABASE_NAME

@Database(entities = [VoiceNoteDTO::class], version = 7)
abstract class AppDatabase: RoomDatabase() {
    abstract fun voiceNotesDao(): VoiceNoteDAO

    companion object{
        @Volatile // All threads have immediate access to this property
        private var instance: AppDatabase? = null
        private val LOCK = Any() // Makes sure no threads making the same thing at the same time
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            ).fallbackToDestructiveMigration()
                .build()
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }
    }
}