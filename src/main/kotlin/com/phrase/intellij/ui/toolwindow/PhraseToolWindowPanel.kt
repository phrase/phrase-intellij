package com.phrase.intellij.ui.toolwindow

import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.phrase.intellij.ui.JTextPaneNoWrap
import java.awt.Color
import javax.swing.JScrollBar
import javax.swing.JTextPane
import javax.swing.SwingUtilities
import javax.swing.text.Style
import javax.swing.text.StyleConstants

class PhraseToolWindowPanel: SimpleToolWindowPanel(true, true){
    private val textStyle: Style
    private val textArea: JTextPane = JTextPaneNoWrap().apply {
        isEditable = false
        textStyle = this.addStyle("textStyle", null)
    }
    private val scrollPane = JBScrollPane(textArea)
    private val scrollBar: JScrollBar = scrollPane.verticalScrollBar

    init {
        add(scrollPane)
    }

    fun clearLog(){
        textArea.text = ""
    }

    fun append(message: String){
        val color = when{
            message.startsWith("Error") -> Color.red
            else -> JBColor.foreground()
        }
        val bold = message.startsWith("Uploading") || message.startsWith("Downloaded")
        StyleConstants.setForeground(textStyle, color)
        StyleConstants.setBold(textStyle, bold)
        textArea.styledDocument.apply{ insertString(length, message, textStyle) }
        //scroll to bottom
        SwingUtilities.invokeLater {
            scrollBar.value = scrollBar.maximum
        }
    }
}