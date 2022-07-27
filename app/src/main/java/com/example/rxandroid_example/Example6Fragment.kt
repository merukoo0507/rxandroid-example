package com.example.rxandroid_example

import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rxandroid_example.common.ExampleItemAdapter
import com.example.rxandroid_example.data.DataService
import com.example.rxandroid_example.viewmodel.ExampleViewmodel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_example3.*
import kotlinx.android.synthetic.main.fragment_example4.*
import kotlinx.android.synthetic.main.fragment_example6.*
import kotlinx.android.synthetic.main.fragment_example6.recyclerview
import kotlinx.coroutines.delay
import timber.log.Timber
import java.util.concurrent.TimeUnit

class Example6Fragment: Fragment(R.layout.fragment_example6){
    val viewModel: ExampleViewmodel by viewModels()
    lateinit var publishSubject: PublishSubject<String>
    var disposable: Disposable?= null

    override fun onStart() {
        super.onStart()

        createIncrement()
        configLayout()
    }

    private fun createIncrement() {
        publishSubject = PublishSubject.create()
        publishSubject
            .debounce(400, TimeUnit.MILLISECONDS)
            .map {
                var colors = DataService.getColorList()
                var results = mutableListOf<String>()
                it.forEach { _char ->
                    colors.forEach { color ->
                        if (color.contains(_char) && !results.contains(color)) results.add(color)
                    }
                }
                return@map results.toList()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: Observer<List<String>> {
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

        editText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                publishSubject.onNext(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.let {
            if (!it.isDisposed) it.dispose()
        }
    }
}
