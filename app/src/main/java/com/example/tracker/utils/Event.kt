package com.example.tracker.utils

class Event<out T>(private val value: T) {

    private var fetched = false

    fun getContent(): T? {
        if (!fetched) {
            fetched = true
            return value
        }
        return null
    }

    fun peekContent(): T? {
        return value
    }
}