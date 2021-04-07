package com.phrase.intellij.ui.projectconfig

class FolderModelItem(val path:String) {

    override fun toString(): String {
        val s = path.removeSuffix("values")
        return "<html>$s<font color=gray>values-*</font></html>"
    }
}