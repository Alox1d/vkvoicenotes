package com.alox1d.vkvoicenotes.di

import android.app.Application
import com.alox1d.vkvoicenotes.App
import com.alox1d.vkvoicenotes.presentation.screen.VoiceListActivity
import com.alox1d.vkvoicenotes.presentation.viewmodel.VoiceListViewModel
import com.android.musicplayer.domain.usecase.*
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DataModule::class, DomainModule::class])
interface AppComponent {

    fun inject(viewModel: VoiceListViewModel)
    fun inject(voiceListActivity: VoiceListActivity)
    fun inject(deleteNoteUseCase: DeleteNoteUseCase)
    fun inject(saveNoteDataUseCase: SaveNoteDataUseCase)
    fun inject(getNotesUseCase: GetNotesUseCase)
    fun inject(syncNotesUseCase: SyncNotesUseCase)
    fun inject(updateNoteDataUseCase: UpdateNoteDataUseCase)

    fun inject(application: App)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}