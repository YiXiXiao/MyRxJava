package xin.itdev.myrxjava

/**
 * 真实被观察者，调用subscribe方法后，当数据发生变化后，用发射器来发送数据
 */
interface ObservableOnSubscribe<T> {
    //实现这个方法后，用发射器来发射数据
    fun subscribe(emitter: ObservableEmitter<T>)
}