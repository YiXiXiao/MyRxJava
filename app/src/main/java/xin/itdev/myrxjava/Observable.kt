package xin.itdev.myrxjava

/**
 * 被观察者
 * 开发者在被观察者发送数据通知观察者时，其实也并不知道要发送什么数据，所以理应也是应该是一个接口，交给开发者自己处理。
 * 但是在RxJava中，ObservableOnSubscribe才是真正的被观察者，所以实际上被观察者为ObservableOnSubscribe
 */
class Observable<T>(private val source: ObservableOnSubscribe<T>) {

    companion object{
        fun <T> create(source: ObservableOnSubscribe<T>): Observable<T> {
            return Observable(source)
        }
    }

    fun subscribe(observer: Observer<T>) {
        //订阅成功就应该先执行订阅方法
        observer.onSubscribe()
        //将发射器与观察者关联
        val emitter: CreateEmitter<T> = CreateEmitter(observer)
        //将真实被观察者发射器关联
        source.subscribe(emitter)
    }

    //map操作符，变换数据后再发送
    fun <R> map(func:(T)->R):Observable<R>{
        //实例化一个新的被观察者，要起到承上启下的作用，所以将真实的被观察者传入。
        // 在新被观察者中需要做的事情就是事件转化，所以将开发者自己实现的转换规则也传入
        val map=ObservableMap(this.source,func)
        return Observable(map)
    }

}