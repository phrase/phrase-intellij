package com.phrase.intellij.ui.toolwindow

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.phrase.intellij.*

class PhraseToolWindow: ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = PhraseToolWindowPanel()
        panel.toolbar = createToolbar().component

        val content = ContentFactory.SERVICE.getInstance().createContent(panel, "", false).apply {
            isCloseable = true
        }
        toolWindow.contentManager.addContent(content)
        toolWindow.activate(null)
    }

    private fun createToolbar(): ActionToolbar {
        val group = DefaultActionGroup().apply {
            add(ActionPush())
            add(ActionPull())
            addSeparator()
            add(ActionConfig())
            addSeparator()
            add(ActionTranslationCenter())
            add(ActionHelp())
        }
        return ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, group, true)
    }

}