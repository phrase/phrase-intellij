package com.phrase.intellij

import com.intellij.ide.util.PropertiesComponent
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object PhrasePrefs {
    var clientPath: String? by NullableStringPref("com.phrase.client_path")

    private class NullableStringPref(private val key: String) : ReadWriteProperty<Any?, String?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): String? = PropertiesComponent.getInstance().getValue(key)
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) = PropertiesComponent.getInstance().setValue(key, value)
    }
}