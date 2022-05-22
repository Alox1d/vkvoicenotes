package com.android.artgallery.domain.usecase.base

import com.alox1d.vkvoicenotes.domain.usecase.base.model.Parameters
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * This abstract class is shared among several closely related UseCase classes
 * that classes that extend this abstract class to use common methods & fields
 **/
abstract class MaybeUseCase<T, P: Parameters> : UseCase() {

    internal abstract fun buildUseCaseMaybe(params:P): Maybe<T>

    fun execute(
        onSuccess: ((t:T) -> Unit),
        onComplete: () -> Unit = {},
        onError: ((t: Throwable) -> Unit),
        params:P
    ) {
        disposeLast()
        lastDisposable = buildUseCaseMaybe(params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onError, onComplete)

        lastDisposable?.let {
            compositeDisposable.add(it)
        }
    }
}
