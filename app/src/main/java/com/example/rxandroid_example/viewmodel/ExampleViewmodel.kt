package com.example.rxandroid_example.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExampleViewmodel: ViewModel() {
    var resultList = MutableLiveData<List<String>>(listOf())
    var resultString = MutableLiveData("")
    var resultInt = MutableLiveData(0)
}