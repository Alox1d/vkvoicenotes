package com.android.artgallery.domain.usecase.base

import com.alox1d.vkvoicenotes.domain.usecase.base.model.Parameters
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * This abstract class is shared among several closely related UseCase classes
 * that classes that extend this abstract class to use common methods & fields
 **/
abstract class CompletableUseCase<P : Parameters> : UseCase() {

    internal abstract fun buildUseCaseCompletable(p:P): Completable

    fun execute(
        onComplete: () -> Unit = {},
        onError: ((t: Throwable) -> Unit),
        p:P
    ) {
        disposeLast()
        lastDisposable = buildUseCaseCompletable(p)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onComplete, onError)

        lastDisposable?.let {
            compositeDisposable.add(it)
        }
    }
}
