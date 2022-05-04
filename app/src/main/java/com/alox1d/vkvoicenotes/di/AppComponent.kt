package com.alox1d.vkvoicenotes.di

import android.app.Application
import com.alox1d.vkvoicenotes.App
import com.alox1d.vkvoicenotes.presentation.screen.VoiceListActivity
import com.alox1d.vkvoicenotes.presentation.viewmodel.VoiceListViewModel
import com.android.musicplayer.domain.usecase.DeleteNoteUseCase
import com.android.musicplayer.domain.usecase.GetNotesUseCase
import com.android.musicplayer.domain.usecase.SaveNoteDataUseCase
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
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(application: App)
}