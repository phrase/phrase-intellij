package com.phrase.intellij

import javax.swing.DefaultComboBoxModel

@Suppress("UNCHECKED_CAST")
fun <T> DefaultComboBoxModel<Any>.getAll():Collection<T>{
    val retval = ArrayList<T>(size)
    for(i in 0 until size) retval += getElementAt(i) as T
    return retval
}