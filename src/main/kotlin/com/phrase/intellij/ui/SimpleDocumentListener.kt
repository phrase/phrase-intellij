package com.phrase.intellij.ui

import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class SimpleDocumentListener(private val callback:()->Unit): DocumentListener {
    override fun insertUpdate(e: DocumentEvent) = callback.invoke()
    override fun removeUpdate(e: DocumentEvent) = callback.invoke()
    override fun changedUpdate(e: DocumentEvent) = callback.invoke()
}