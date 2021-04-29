package xin.itdev.myrxjava

interface Emitter<T> {
    fun onNext(value:T)
    fun onComplete()
    fun onError(e:Throwable)
}