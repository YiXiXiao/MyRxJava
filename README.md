# MyRxJava
手撸一个RxJava demo
在看RxJava源码的过程中发生了以下的事情
![艹.gif](https://upload-images.jianshu.io/upload_images/4292655-ac9833ee1b84a4cb.gif?imageMogr2/auto-orient/strip)
头昏脑涨，怀疑人生，生无可恋还有啥欢迎补充！！
国际惯例，抛出一些问题先
####问：说说你对RxJava的理解
答：RxJava是一个基于观察者模式设计的开源库，它的强大地方主要在于链式调用的api、操作符以及线程切换，在异步任务中，不再需要那种callBack方式的无限嵌套。
面试官：
![image.png](https://upload-images.jianshu.io/upload_images/4292655-7b949deab4392ac0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
你：大佬，等等，看看我自己撸的MyRxJava先吧，你这问题我不知道该怎么回答。[传送门]()

首先要知道什么是观察者模式：**定义对象间的一种一对多的依赖关系，当一个对象的状态发生改变时，所有依赖于它的对象都得到通知。** 例如：微信公众号，一群人关注一个公众号，当公众号内容有更新的时候就会发出通知，关注者就能知道内容更新了，这里面人就是观察者，公众号就是被观察者，被观察者数据发生改变时，观察者能得到通知。

既然要自己撸一个RxJava，那先看看RxJava如何使用的：
````
        Observable.create(object : ObservableOnSubscribe<String>{
            override fun subscribe(emitter: ObservableEmitter<String>) {
                emitter.onNext("真实观察者在发送东西啦")
                emitter.onComplete()
            }
        }).subscribe(object : Observer<String>{
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
            }

            override fun onError(e: Throwable) {
            }
        })
````
通过上面，我们知道RxJava中的观察者模式两个角色、一个动作、四个事件：
**1、Observable：被观察者**
**2、Observer：观察者**
**3、Subscribe：订阅**
**4、Event：被观察者通知观察者的事件（onNext()、onError()、onComplete()、onSubscribe()）**

**一、**新建一个Observable、Observer两个类
````
/**
 * 被观察者
 * 开发者在被观察者发送数据通知观察者时，其实也并不知道要发送什么数据，所以理应也是应该是一个接口，交给开发者自己处理。
 * 但是在RxJava中，ObservableOnSubscribe才是真正的被观察者，所以实际上被观察者为ObservableOnSubscribe
 */
class Observable {
}

/**
 * 观察者
 * 在开发者使用时，这个观察者收到被观察者通知时并不知道要做什么事情，所以这里是一个接口
 */
interface Observer {
}

/**
 * 真实被观察者
 */
interface ObservableOnSubscribe{
}

````
实际上，RxJava并不知道观察者收到被观察者数据更新通知时要做什么，所以设计成了接口，让开发者自己去实现。同样的被观察者也一样，实际上RxJava真正的被观察者为：ObservableOnSubscribe，意为订阅时的被观察者，所以这个才是真正的被观察者。

**二、**主要的类有了，想想RxJava是怎么使用的，首先先调用create静态方法传入真实被观察者的实现返回被观察者，之后调用subscribe方法传入观察者的实现（被观察者和观察者建立联系的步骤）。同时，我们知道真实被观察者及观察者的之间要传递的数据类型都不确定，所以需要使用泛型。还有被观察者通知观察者的四种事件。修改一下代码：
````
//1、修改真实被观察者支持泛型
interface ObservableOnSubscribe<T>{
}

class Observable {
    companion object{
        //3、被观察者中新建静态方法create，接收真实被观察者，返回被观察者
        fun <T> create(source: ObservableOnSubscribe<T>): Observable {
            return Observable()
        }
    }
    //4、被观察者和观察者建立联系
    fun <T> subscribe(observer: Observer<T>) {

    }
}

//2、观察者增加四种事件类型，支持泛型
interface Observer<T> {
    fun onNext(value:T)
    fun onComplete()
    fun onError(e:Throwable)
    fun onSubscribe()
}
````
**三、**到这里，真实被观察者目前还只是一个接口类，开发者不知道如何发送数据，RxJava中将发送数据交给了发射器ObservableEmitter:Emitter，并且提供一个待开发者实现的方法，在方法中利用发射器进行数据发送。
````
//真实被观察者添加一个待实现方法，开发者实现后可以使用发射器来发送数据
interface ObservableOnSubscribe<T> {
    //实现这个方法后，用发射器来发射数据
    fun subscribe(emitter:ObservableEmitter<T>)
}

//被观察者发射器
interface ObservableEmitter<T> : Emitter<T> {
    //实际上这里还有很多的其他方法，如是否可以取消，是否处理了等等，这里不做分析了
}
//发射器
interface Emitter<T> {
    fun onNext(value:T)
    fun onComplete()
    fun onError(e:Throwable)
}
````
来，我们试试使用：
````
        Observable.create(object : ObservableOnSubscribe<String>{
            override fun subscribe(emitter: ObservableEmitter<String>) {
                emitter.onNext("开始尝试发射数据了")
                emitter.onComplete()
            }
        }).subscribe(object : Observer<String>{
            override fun onNext(value: String) {
                Log.e("XYX",value)
            }

            override fun onComplete() {
                //RxJava中如果调用了onComplete或onError方法之后，再发送数据就无效
                Log.e("XYX", "完成了")
            }

            override fun onError(e: Throwable) {
                Log.e("XYX", "出错了")
            }

            override fun onSubscribe() {
                Log.e("XYX", "订阅了")
            }
        })
````
什么？啥也没打印？
可不么，真实被观察者利用发射器调用onNext()方法发送数据，往哪里发送没告诉他，执行了也不报错，但是程序也不知道怎么处理啊。还有被观察者subscribe方法只有方法，没有方法体，程序也不知道如何做关联订阅啊~

**四、**接下来还要实现发射器往观察者中发射数据，怎么发？当然是直接调用方法发了，怎么调用方法？拿到观察者的引用不就行了？
发射器是一个接口，新建一个发射器的实现类，接收观察者对象，并实现发射器的三个方法：
````
class CreateEmitter<T>(val observer: Observer<T>) : ObservableEmitter<T> {
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

class Observable {
    
    companion object{
        fun <T> create(source: ObservableOnSubscribe<T>): Observable {
            return Observable()
        }
    }
    
    fun <T> subscribe(observer: Observer<T>) {
        val emitter: CreateEmitter<T> = CreateEmitter(observer)
    }
}
````
这里就将观察者和发射器关联，但是发射器还没有和真实被观察者关联，也应该进行关联，要不然被观察中就无法将数据发送给观察者了，怎么关联？没错，create方法传入了一个真实被观察者对象，就它了。
![image.png](https://upload-images.jianshu.io/upload_images/4292655-0421f2a661504be4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
想想，是先create方法创建一个被观察者，之后才执行的订阅，就算把发射器抽成全局，create方法的发射器还没初始化，且发射器的初始化需要关联观察者，怎么办？简单，把这个create传进来的真实被观察者存起来就行了。这里通过修改Observable构造方法的方式来进行修改：
````
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
}
````  
这不，手撸一个RxJava就完成了！
什么？完成了？不信你看看：
![image.png](https://upload-images.jianshu.io/upload_images/4292655-b3910d154fdb9851.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
其实，这里只是实现了RxJava中一点点点点，实现了一个观察者模式。
总结一下：
**1、使用被观察者的静态方法create创建一个真正的被观察者对象，然后设置到被观察者的source对象中**
**2、调用被观察者的subscribe方法传入观察者对象实现观察者与被观察者的订阅关系，并立马调用观察者的onSubscribe方法通知订阅成功，然后创建发射器传入观察者，使观察者和发射器关联。**
**3、真正的被观察者subscribe方法中通过发射器调用观察者的方法发送数据。**
**4、观察者收到数据**
![image.png](https://upload-images.jianshu.io/upload_images/4292655-d75e02908720da7f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
RxJava中还有操作符，既然都到这了，要不就再实现一个操作符来玩玩？说干就要干，裤子都脱了，不干是傻瓜！
RxJava中有各种各样的操作符，例如：map、just、range等等，具体可以搜索一下，如果你比较懒，那就点击这里[RxJava操作符](https://www.baidu.com/s?ie=UTF-8&wd=RxJava%E6%93%8D%E4%BD%9C%E7%AC%A6)
我们实现一个操作符，就挑个常用的map吧。

**实现一个map操作符**

RxJava的map操作符有转换的功能，实际上就是将被观察者的数据换了，返回一个被观察者后继续执行，所以这里应该是在create调用之后的方法，而且返回的是新的数据类型。
````
    fun <R> map():Observable<R>{
        //返回的新的观察者要起到承上启下的作用
    }
````
新的真实观察者就叫ObservableMap吧，RxJava中有各种各样Observablexxx，就是使用装饰者模式来设计的（在不改变原有对象的基础之上，将功能附加到对象上。提供了比继承更有弹性的替代方案、扩展原有对象功能），这个类要做的就是获取上级的被观察者，经过自身转化后再与下级观察者实现订阅关系即：**要有一个观察者观察上级被观察者，同时是有一个被观察者，能被下级观察者观察**
新建ObservableMap：
````
/**
 * 创建一个用于被下级观察的被观察者
 * 接收上级真实被观察者，以及数据变换规则的高阶函数
 * 这里要两个泛型，一个是原始数据T，一个是变换后的数据R
 * 实现真实的被观察
 */
class ObservableMap<T, R>(private val source:ObservableOnSubscribe<T>, private val func:((T)->R)):ObservableOnSubscribe<R>{
}
````
上面说了 map方法是要返回一个被观察者，起到承上启下的作用 
修改一下map方法 
````
    fun <R> map(func:(T)->R):Observable<R>{
        //实例化一个新的被观察者，要起到承上启下的作用，所以将真实的被观察者传入。
        // 在新被观察者中需要做的事情就是事件转化，所以将开发者自己实现的转换规则也传入
        val map=ObservableMap(this.source,func)
        return Observable(map)
    }
````
ObservableMap类中要实现观察上级被观察者数据，同时要被下级观察者观察，所以里面肯定也要有一个观察者，用于观察上级真实被观察者source，首先来创建一个观察者:
````
    class MapObserver<T,R>(private val emitter:ObservableEmitter<R>, private val func:((T)->R)):Observer<T>{

        override fun onNext(item: T) {
            //在发送数据前按照开发者设置的数据变换规则获取结果
            val result=func.invoke(item)
            //利用发射器发送最新的数据
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
````
接下来想想 ObservableMap实现了ObservableOnSubscribe这个真实被观察者，subscribe方法中需要做哪些是：
1、source需要设置新的发射器
2、创建新的发射器要有一个观察者 
3、创建新的观察者对象 
````
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
````
最终的代码如下：
````
    //-----------Observable类中增加map方法：
    //map操作符，变换数据后再发送
    fun <R> map(func:(T)->R):Observable<R>{
        //实例化一个新的被观察者，要起到承上启下的作用，所以将真实的被观察者传入。
        // 在新被观察者中需要做的事情就是事件转化，所以将开发者自己实现的转换规则也传入
        val map=ObservableMap(this.source,func)
        return Observable(map)
    }

    //新增ObservableMap类：

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

//使用 
Observable.create(object:ObservableOnSubscribe<String>{
            override fun subscribe(emitter: ObservableEmitter<String>) {
                emitter.onNext("开始尝试发射数据了")
                emitter.onComplete()
            }
        }).map {
            "经过map后的数据" + it
        }.subscribe(object : Observer<String>{
            override fun onNext(value: String) {
                Log.e("XYX", value)
            }

            override fun onComplete() {
                Log.e("XYX", "完成了")
            }

            override fun onError(e: Throwable) {
                Log.e("XYX", "出错了")
            }

            override fun onSubscribe() {
                Log.e("XYX", "订阅了")
            }
        })
````
操作符map就做完了，我们来看看结果：
![image.png](https://upload-images.jianshu.io/upload_images/4292655-ed25ae8e3f7c59be.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

//TODO 关于RxJava切换线程的内容，将来再补吧，RxJava真的是一个强大而复杂的开源框架，也不好理解。。。
