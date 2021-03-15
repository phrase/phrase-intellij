package com.phrase.intellij.ui.projectconfig

import javax.swing.*

class JCheckboxList:JPanel() {

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
    }

    private val map = HashMap<String, JCheckBox>()

    fun addAll(paths:Collection<String>){
        for(path in paths){
            if(map.keys.contains(path)) continue
            val checkBox = JCheckBox(path, null, true)
            add(checkBox)
            map[path] = checkBox
        }
        repaint()
        revalidate()
    }

    fun getCheckedPaths():Collection<String> = map.filter { it.value.isSelected }.keys

    fun hasCheckedPaths():Boolean = map.any { it.value.isSelected }
}