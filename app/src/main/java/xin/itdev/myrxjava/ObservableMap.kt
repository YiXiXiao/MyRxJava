package xin.itdev.myrxjava

/**
 * 创建一个用于被下级观察的被观察者
 * 接收上级真实被观察者，以及数据变换规则的高阶函数
 * 这里要两个泛型，一个是原始数据T，一个是变换后的数据R
 * 实现真实的被观察
 */
class ObservableMap<T, R>(private val source:ObservableOnSubscribe<T>, private val func:((T)->R)):ObservableOnSubscribe<R>{

    override fun subscribe(emitter: ObservableEmitter<R>) {
        //创建真实观察者，在真实观察者中接收原始数据后，按照高阶函数定义的修改规则获取最新的结果后重新发射数据
        val map=MapObserver(emitter, func)
        //用新的MapObserver重新调用onSubscribe
        map.onSubscribe()
        //创建新的发射器，用新的发射器来发送数据
        val createEmitter = CreateEmitter(map)
        //最终在新的被观察者的subscribe方法中，使用新的发射器进行发送数据
        source.subscribe(createEmitter)
    }

    class MapObserver<T,R>(private val emitter:ObservableEmitter<R>, private val func:((T)->R)):Observer<T>{

        override fun onNext(item: T) {
            val result=func.invoke(item)
            emitter.onNext(result)
        }

        override fun onError(e: Throwable) {
            emitter.onError(e)
        }

        override fun onComplete() {
            emitter.onComplete()
        }

        override fun onSubscribe() {

        }

    }

}