package com.example.rxandroid_example

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rxandroid_example.common.ExampleItemAdapter
import com.example.rxandroid_example.data.DataService
import com.example.rxandroid_example.viewmodel.ExampleViewmodel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_example2.*
import kotlinx.android.synthetic.main.fragment_example3.*
import kotlinx.android.synthetic.main.fragment_example3.recyclerview
import org.reactivestreams.Subscription

class Example3Fragment: Fragment(R.layout.fragment_example3){
    val viewModel: ExampleViewmodel by viewModels()
    lateinit var single: Single<List<String>>
    var disposable: Disposable ?= null

    override fun onStart() {
        super.onStart()

        createObservable()
        configLayout()
    }

    private fun createObservable() {
        single = Single.create { emitter ->
            var colors = DataService.getAsyncColorList()
//            if (colors.isEmpty()) {
                emitter.onError(Throwable("Colors not found."))
                return@create
//            }
            emitter.onSuccess(colors)
        }
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<List<String>> {
                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onSuccess(t: List<String>) {
                    viewModel.resultList.value = t
                }

                override fun onError(e: Throwable) {
                    viewModel.resultString.value = e.message
                }
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

        viewModel.resultString.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                textView.visibility = View.GONE
                return@observe
            }
            textView.visibility = View.VISIBLE
            textView.text = it
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.let {
            if (!it.isDisposed) it.dispose()
        }
    }
}