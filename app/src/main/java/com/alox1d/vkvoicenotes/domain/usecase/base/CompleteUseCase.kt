package com.android.artgallery.domain.usecase.base

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * This abstract class is shared among several closely related UseCase classes
 * that classes that extend this abstract class to use common methods & fields
 **/
abstract class CompleteUseCase : UseCase() {

    internal abstract fun buildUseCaseCompletable(): Completable

    fun execute(
        onComplete: () -> Unit = {},
        onError: ((t: Throwable) -> Unit),
    ) {
        disposeLast()
        lastDisposable = buildUseCaseCompletable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onComplete, onError)

        lastDisposable?.let {
            compositeDisposable.add(it)
        }
    }
}
