package xin.itdev.myrxjava

class CreateEmitter<T>(private var observer: Observer<T>) :
    ObservableEmitter<T> {

    override fun onNext(value: T) {
        observer.onNext(value)
    }

    override fun onComplete() {
        observer.onComplete()
    }

    override fun onError(e: Throwable) {
        observer.onError(e)
    }


}