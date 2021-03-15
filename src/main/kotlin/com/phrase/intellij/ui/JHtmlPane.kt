package com.phrase.intellij.ui

import com.phrase.intellij.Utils
import javax.swing.JTextPane
import javax.swing.event.HyperlinkEvent
import javax.swing.text.html.HTMLDocument
import javax.swing.text.html.HTMLEditorKit

class JHtmlPane: JTextPane() {

    init {
        addHyperlinkListener{
            if (it.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                Utils.openLink(it.url.toString())
            }
        }
    }

    fun setHtml(html: String) {
        this.contentType = "text/html"
        val doc = this.document as HTMLDocument
        doc.remove(0, doc.length)
        val editorKit = this.editorKit as HTMLEditorKit
        try {
            editorKit.insertHTML(doc, doc.length, html, 0, 0, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}