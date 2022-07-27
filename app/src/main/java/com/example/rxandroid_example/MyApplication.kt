package com.example.rxandroid_example

import android.app.Application
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import timber.log.Timber

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        RxJavaPlugins.setErrorHandler(Timber::e)
    }
}