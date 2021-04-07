package com.phrase.intellij.api

interface ApiCallback {
    fun onMessage(message:String)
    fun onFinish()
}