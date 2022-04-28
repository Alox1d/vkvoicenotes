package com.android.artgallery.domain.usecase.base

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * This abstract class is shared among several closely related UseCase classes
 * that classes that extend this abstract class to use common methods & fields
 **/
abstract class MaybeUseCase<T> : UseCase() {

    internal abstract fun buildUseCaseMaybe(): Maybe<T>

    fun execute(
        onSuccess: ((t:T) -> Unit),
        onComplete: () -> Unit = {},
        onError: ((t: Throwable) -> Unit),
    ) {
        disposeLast()
        lastDisposable = buildUseCaseMaybe()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onError, onComplete)

        lastDisposable?.let {
            compositeDisposable.add(it)
        }
    }
}
