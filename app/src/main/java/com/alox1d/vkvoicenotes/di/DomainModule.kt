package com.alox1d.vkvoicenotes.di

import com.android.musicplayer.domain.repository.VoiceListRepository
import com.android.musicplayer.domain.usecase.*
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

    @Singleton
    @Provides
    fun provideUpdateNoteUseCase(
        voiceListRepository: VoiceListRepository
    ): UpdateNoteDataUseCase {
        return UpdateNoteDataUseCase(voiceListRepository)
    }
}