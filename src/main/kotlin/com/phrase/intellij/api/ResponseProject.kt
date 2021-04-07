package com.phrase.intellij.api

data class ResponseProject(
    val id:String,
    val name:String,
    val slug:String,
){
    //for ComboBox model
    override fun toString() = name
}