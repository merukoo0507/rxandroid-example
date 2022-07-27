package com.example.rxandroid_example

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rxandroid_example.common.ExampleItemAdapter
import com.example.rxandroid_example.data.DataService
import com.example.rxandroid_example.viewmodel.ExampleViewmodel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_example1.*

class Example1Fragment: Fragment(R.layout.fragment_example1) {
    val viewModel: ExampleViewmodel by viewModels()
    lateinit var observable: Observable<List<String>>

    override fun onStart() {
        super.onStart()

        createObservable()
        configLayout()
    }

    private fun createObservable() {
        observable = Observable.just(DataService.getColorList())
        observable.subscribe(object: Observer<List<String>>{
                override fun onSubscribe(d: Disposable) {}

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
}