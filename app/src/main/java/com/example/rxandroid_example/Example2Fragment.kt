package com.example.rxandroid_example

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rxandroid_example.common.ExampleItemAdapter
import com.example.rxandroid_example.data.DataService
import com.example.rxandroid_example.viewmodel.ExampleViewmodel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_example2.*
import org.reactivestreams.Subscription

class Example2Fragment: Fragment(R.layout.fragment_example2) {
    val viewModel: ExampleViewmodel by viewModels()
    lateinit var observable: Observable<List<String>>
    var disposable: Disposable ?= null

    override fun onStart() {
        super.onStart()

        createObservable()
        configLayout()
    }

    private fun createObservable() {
        observable = Observable.fromCallable { DataService.getAsyncColorList() }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: Observer<List<String>>{
                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onNext(t: List<String>) {
                    viewModel.resultList.value = t
                }

                override fun onError(e: Throwable) {}

                override fun onComplete() {}
            })
    }

    private fun configLayout() {
        var adapter = ExampleItemAdapter(listOf())
        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(context)

        viewModel.resultList.observe(viewLifecycleOwner) {
            adapter.items = it
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.let {
            if (!it.isDisposed) it.dispose()
        }
    }
}
