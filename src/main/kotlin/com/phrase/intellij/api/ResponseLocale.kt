package com.phrase.intellij.api

data class ResponseLocale(
    val id:String,
    val name:String,
    val code:String,
    val default:Boolean,
){
    //for ComboBox model
    override fun toString() = name
}