package xin.itdev.myrxjava

/**
 * 观察者
 * 在开发者使用时，这个观察者收到被观察者通知时并不知道要做什么事情，所以这里是一个接口
 */
interface Observer<T> {
    fun onNext(value:T)
    fun onComplete()
    fun onError(e:Throwable)
    fun onSubscribe()
}