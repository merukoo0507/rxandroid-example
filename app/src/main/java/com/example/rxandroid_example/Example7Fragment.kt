package com.example.rxandroid_example

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.rxandroid_example.viewmodel.ExampleViewmodel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_example7.*
import org.reactivestreams.Subscription
import timber.log.Timber
import java.lang.Exception
import java.util.concurrent.TimeUnit

class Example7Fragment: Fragment(R.layout.fragment_example7) {
    val TEST_NUM = 10000000
    val viewModel: ExampleViewmodel by viewModels()
    var compositeDisposable = CompositeDisposable()

    override fun onStart() {
        super.onStart()
        configLayout()
    }

    /*
        被觀察者發生事件的速度太快，觀察者來不及接受所有的事件，從而緩存區中的事件越積越多，
        最終導致緩存區溢出，事件丟失並OOM(out of memory)
        若不停更新畫面，也會導致ANR
     */
    private fun createBackPressure() {
        var disposable = Observable.range(1, TEST_NUM)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                showLog("onNext --> $it")
            }
        compositeDisposable.add(disposable)
    }

    /*
        觀察者不接收事件的情況下，被觀察者繼續發送事件 & 存放到緩存區；
        再按需求取出
        若在主線程接收callback，observeOn(AndroidSchedulers.mainThread())，
        則為同步操作
     */
    private fun createFlowable() {
        var disposable = Flowable
            .create<Int>({
                var i = 0
                while (true) {
                    i++
                    it.onNext(i)
//                    Thread.sleep(100)
                }
            }, BackpressureStrategy.LATEST)
//            .range(1, TEST_NUM)
//            .onBackpressureBuffer(20, {}, BackpressureOverflowStrategy.DROP_OLDEST)
            //指定Buffer: 128+20
            // 當全部的資料被暫存，會OOM而Crash
            // , BackpressureStrategy.BUFFER)
            // 128Buffer空間塞滿時Drop掉以後的資料，因此繼續請求後，中間會有一段資料被遺落
            // , BackpressureStrategy.DROP)
            // 若無限發送資料會拋出出錯，onError --> create: could not emit value due to lack of requests
            // , BackpressureStrategy.ERROR)
            // 預設，沒有Strategy，拋出錯誤顯示Queue is full...
            // , BackpressureStrategy.MISS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                showLog("onNext --> $it", true)
            }
        compositeDisposable.add(disposable)
    }

    private fun showLog(s: String, showText: Boolean = true) {
        Timber.d(s)
        if (showText)
            viewModel.resultString.value = "$s\n"   // + viewModel.resultString.value
    }

    private fun configLayout() {
        createBackPressure.setOnClickListener {
            createBackPressure()
        }
        createFlowable.setOnClickListener {
            createFlowable()
        }
        clear.setOnClickListener {
            if (!compositeDisposable.isDisposed)
                compositeDisposable.clear()
        }

        viewModel.resultString.observe(viewLifecycleOwner) {
            textView.text = it
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed)
            compositeDisposable.clear()
    }
}