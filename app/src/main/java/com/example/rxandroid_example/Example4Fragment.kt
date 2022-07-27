package com.example.rxandroid_example

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rxandroid_example.common.ExampleItemAdapter
import com.example.rxandroid_example.data.DataService
import com.example.rxandroid_example.viewmodel.ExampleViewmodel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_example4.*

class Example4Fragment: Fragment(R.layout.fragment_example4){
    val viewModel: ExampleViewmodel by viewModels()
    lateinit var publishSubject: PublishSubject<Int>
    var compositeDisposable = CompositeDisposable()
    var counter = 0

    override fun onStart() {
        super.onStart()

        createIncrement()
        configLayout()
    }

    private fun createIncrement() {
        publishSubject = PublishSubject.create()
        publishSubject.subscribe(object: Observer<Int> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: Int) {
                    viewModel.resultInt.value = t
                }

                override fun onError(e: Throwable) {}

                override fun onComplete() {}
            })
    }

    private fun AddSubscribe2() {
        publishSubject.subscribe(object: Observer<Int> {
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onNext(t: Int) {
                textView2.text = "$t"
            }

            override fun onError(e: Throwable) {}

            override fun onComplete() {}
        })
    }

    private fun configLayout() {
        increment_button.setOnClickListener {
            counter++
            publishSubject.onNext(counter)
        }
        btn_subscribe2.setOnClickListener {
            AddSubscribe2()
        }

        viewModel.resultInt.observe(viewLifecycleOwner) {
            textView.text = it.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }
}
