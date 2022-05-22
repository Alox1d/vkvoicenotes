package com.android.artgallery.domain.usecase.base

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * This abstract class is shared among several closely related UseCase classes
 * that classes that extend this abstract class to use common methods & fields
 **/
abstract class FlowableUseCase<T> : UseCase() {

    internal abstract fun buildUseCaseFlowable(): Flowable<T>

    fun execute(
        onNext: ((t: T) -> Unit),
        onError: ((t: Throwable) -> Unit),
        onComplete: () -> Unit = {}
    ) {
        disposeLast()
        lastDisposable = buildUseCaseFlowable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onNext, onError, onComplete)

        lastDisposable?.let {
            compositeDisposable.add(it)
        }
    }
}
