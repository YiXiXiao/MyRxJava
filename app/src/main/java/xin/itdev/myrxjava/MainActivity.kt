package xin.itdev.myrxjava

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

    }
}
