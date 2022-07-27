package com.example.rxandroid_example

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.rxandroid_example.data.DataService
import com.example.rxandroid_example.viewmodel.ExampleViewmodel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_example5.*

class Example5Fragment: Fragment(R.layout.fragment_example5){
    val viewModel: ExampleViewmodel by viewModels()
    var disposable: Disposable?= null

    override fun onStart() {
        super.onStart()

        createSingle2Map()
        createZipMap()
        configLayout()
    }

    private fun createSingle2Map() {
        Single.just(1)
            .map{
                return@map "$it to ${DataService.getColorList()[it]}"
            }
            .subscribe(object: SingleObserver<String> {
                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onSuccess(t: String) {
                    viewModel.resultString.value = viewModel.resultString.value + "$t\n"
                }

                override fun onError(e: Throwable) {}
            })
    }

    private fun createZipMap() {
        Observable.just(1, 2)
            .zipWith(Observable.just(3, 4)) { t1, t2 ->
                return@zipWith "$t1, $t2"
            }
            .subscribe(object: Observer<String> {
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: String) {
                    viewModel.resultString.value = viewModel.resultString.value + "$t\n"
                }

                override fun onError(e: Throwable) {}

                override fun onComplete() {}

            })
    }

    private fun configLayout() {
        viewModel.resultString.observe(viewLifecycleOwner) {
            textView.text = it.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.let {
            if (!it.isDisposed) it.dispose()
        }
    }
}
