package com.alox1d.vkvoicenotes.data.repository

import com.alox1d.vkvoicenotes.data.database.AppDatabase
import com.alox1d.vkvoicenotes.data.model.mapToDTO
import com.alox1d.vkvoicenotes.data.model.mapToDomain
import com.alox1d.vkvoicenotes.data.remote.VKService
import com.alox1d.vkvoicenotes.domain.model.VoiceNote
import com.android.musicplayer.domain.repository.VoiceListRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class VoiceListRepositoryImp(private val appDatabase: AppDatabase) : VoiceListRepository {

    override fun delete(voiceNote: VoiceNote?): Completable {
        return appDatabase.voiceNotesDao().delete(voiceNote?.mapToDTO())
    }

    override fun getVoiceNotes(): Flowable<List<VoiceNote>> {
        // map вынесен в репозиторий, остальное - в use-case (base)
        return appDatabase.voiceNotesDao().loadAll().map { list -> list.map { it.mapToDomain() } }
    }

    override fun saveVoiceNotes(voiceNote: VoiceNote?):Single<Long> {
        return appDatabase.voiceNotesDao().insert(voiceNote?.mapToDTO())
    }
    override fun syncVoicesNotes(notes: List<VoiceNote>):Completable {
        return Completable.fromCallable {
            (notes.map { VKService().uploadDoc(it.mapToDTO()) })
//            map{
                // Todo Где лучше преобразовывать в Uri и как передавать контекст?
//                Uri.parse(PathUtils.getPath(this, it.mapToDTO().path))
//                 })
        }

    }
}