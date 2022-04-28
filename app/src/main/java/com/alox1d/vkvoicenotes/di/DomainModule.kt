package com.alox1d.vkvoicenotes.di

import android.app.Application
import com.alox1d.vkvoicenotes.data.database.AppDatabase
import com.alox1d.vkvoicenotes.data.database.VoiceNoteDAO
import com.alox1d.vkvoicenotes.data.repository.VoiceListRepositoryImp
import com.android.musicplayer.domain.repository.VoiceListRepository
import com.android.musicplayer.domain.usecase.DeleteNoteUseCase
import com.android.musicplayer.domain.usecase.GetNotesUseCase
import com.android.musicplayer.domain.usecase.SaveNoteDataUseCase
import com.android.musicplayer.domain.usecase.SyncNotesUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

//Methods in @Module provide something,
// so start with @Provides then write your method.
// It is standard that those method names start with provideAnyName().
// The return type of the method is very important.
// Whenever another provideMethod() in AppModule needs an argument to instantiate something.
// Dagger will look at the return type of the other methods in AppModule.
// If a correct return type is there, Dagger will automatically link those methods.
@Module
class DomainModule {

    @Singleton
    @Provides
    fun provideSaveSongDataUseCase(
        voiceListRepository: VoiceListRepository
    ): SaveNoteDataUseCase {
        return SaveNoteDataUseCase(voiceListRepository)
    }

    @Singleton
    @Provides
    fun provideDeleteSongUseCase(
        voiceListRepository: VoiceListRepository
    ): DeleteNoteUseCase {
        return DeleteNoteUseCase(voiceListRepository)
    }

    @Singleton
    @Provides
    fun provideGetSongsUseCase(
        voiceListRepository: VoiceListRepository
    ): GetNotesUseCase {
        return GetNotesUseCase(voiceListRepository)
    }

    @Singleton
    @Provides
    fun provideSyncNotesUseCase(
        voiceListRepository: VoiceListRepository
    ): SyncNotesUseCase {
        return SyncNotesUseCase(voiceListRepository)
    }
}