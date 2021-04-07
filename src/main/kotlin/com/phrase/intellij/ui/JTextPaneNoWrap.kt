package com.phrase.intellij.ui

import javax.swing.JTextPane
import java.awt.Dimension

class JTextPaneNoWrap: JTextPane() {
    override fun getScrollableTracksViewportWidth(): Boolean {
        // Only track viewport width when the viewport is wider than the preferred width
        return (getUI().getPreferredSize(this).width <= parent.size.width)
    }

    override fun getPreferredSize(): Dimension? {
        // Avoid substituting the minimum width for the preferred width when the viewport is too narrow
        return getUI().getPreferredSize(this)
    }
}