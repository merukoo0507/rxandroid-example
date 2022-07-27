package com.example.rxandroid_example.data

import java.lang.Exception

object DataService {
    fun getColorList() = listOf(
        "blue",
        "green",
        "red",
        "brown",
        "black",
    )

    fun getAsyncColorList(): List<String> {
        try {
            Thread.sleep(3000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return getColorList()
    }
}