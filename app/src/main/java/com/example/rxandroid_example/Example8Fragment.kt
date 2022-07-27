package com.example.rxandroid_example

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.rxandroid_example.viewmodel.ExampleViewmodel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observables.ConnectableObservable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_example8.*
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import timber.log.Timber
import java.util.concurrent.TimeUnit

class Example8Fragment: Fragment(R.layout.fragment_example8) {
    val viewModel: ExampleViewmodel by viewModels()
    var compositeDisposable = CompositeDisposable()
    lateinit var connectableObservable: ConnectableObservable<Long>

    override fun onStart() {
        super.onStart()

        createConnectableObservable()
        configLayout()
    }

    private fun createConnectableObservable() {
        connectableObservable = Observable.interval(1, TimeUnit.SECONDS).publish()
        connectableObservable.connect()
        connectableObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Long> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: Long) {
                    addText("Observer1 onNext --> $t")
                }

                override fun onError(e: Throwable) {
                    addText("Observer1 onError --> ${e?.message}")
                }

                override fun onComplete() {
                    addText("Observer1 onComplete")
                }
            })
    }

    private fun createObserver2() {
        connectableObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Long> {
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onNext(t: Long) {
                addText("Observer2 onNext --> $t")
            }

            override fun onError(e: Throwable) {
                addText("Observer2 onError --> ${e?.message}")
            }

            override fun onComplete() {
                addText("Observer2 onComplete")
            }
        })
    }

    private fun addText(s: String) {
        Timber.d(s)
        viewModel.resultString.value = viewModel.resultString.value + "$s\n"
    }

    private fun configLayout() {
        observer2.setOnClickListener {
            createObserver2()
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