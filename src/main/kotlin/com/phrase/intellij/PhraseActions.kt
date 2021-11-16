package com.phrase.intellij

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.NlsActions
import com.intellij.openapi.wm.ToolWindowManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import com.phrase.intellij.api.Api
import com.phrase.intellij.api.ApiCallback
import com.phrase.intellij.ui.projectconfig.ProjectConfigDialog
import com.phrase.intellij.ui.toolwindow.PhraseToolWindowPanel

enum class PhraseButton(val title:String, val desc:String, val icon:String) {
    PUSH(PhraseBundle.message("buttonPush"), PhraseBundle.message("buttonPushDesc"), "/icons/cloud-upload.png"),
    PULL(PhraseBundle.message("buttonPull"), PhraseBundle.message("buttonPullDesc"), "/icons/cloud-download.png"),
    CONFIG(PhraseBundle.message("buttonConfig"), PhraseBundle.message("buttonConfigDesc"), "/icons/cog.png"),
    TRANSLATION_CENTER(PhraseBundle.message("buttonTranslationCenter"), PhraseBundle.message("buttonTranslationCenterDesc"), "/icons/home.png"),
    HELP(PhraseBundle.message("buttonHelp"), PhraseBundle.message("buttonHelpDesc"), "/icons/question.png");
}

abstract class PhraseAction(button: PhraseButton):AnAction(button.title, button.desc, IconLoader.getIcon(button.icon, PhraseAction::class.java)){
    final override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { act(it) }
    }
    abstract fun act(project:Project)
}

abstract class PhraseActionToolWindow(button: PhraseButton):PhraseAction(button){
    final override fun act(project: Project) {
        ToolWindowManager.getInstance(project).getToolWindow("Phrase")?.let{
            if (!it.isActive) it.activate(null)
            val panel = it.contentManager.contents[0].component as PhraseToolWindowPanel
            act(project, panel)
        }
    }
    abstract fun act(project: Project, panel: PhraseToolWindowPanel)
}

class ActionPush: PhraseActionToolWindow(PhraseButton.PUSH) {
    override fun act(project: Project, panel: PhraseToolWindowPanel) = onPushPull(Api.Command.PUSH, project, panel)
}
class ActionPull: PhraseActionToolWindow(PhraseButton.PULL) {
    override fun act(project: Project, panel: PhraseToolWindowPanel) = onPushPull(Api.Command.PULL, project, panel)
}
class ActionConfig: PhraseAction(PhraseButton.CONFIG) {
    override fun act(project: Project) = onConfig(project)
}
class ActionTranslationCenter: PhraseAction(PhraseButton.TRANSLATION_CENTER) {
    override fun act(project: Project) = Utils.openLink(PhraseUrls.TRANSLATION_CENTER, project)
}
class ActionHelp: PhraseAction(PhraseButton.HELP) {
    override fun act(project: Project) = Utils.openLink(PhraseUrls.HELP, project)
}
class ActionOpenSettings : AnAction(PhraseBundle.message("actionOpenSettings")) {
    override fun actionPerformed(p0: AnActionEvent) {
        Utils.openConfigurable()
    }
}


private fun onConfig(project: Project){
    try {
        Api.ensureClientExists()
        val dialog = ProjectConfigDialog(project)
        val isOK = dialog.showAndGet()
        if(isOK) {
            dialog.save()
            project.guessProjectDir()?.refresh(false, false)
            YamlParser.yamlVirtualFile(project)?.let {
                FileEditorManager.getInstance(project).openTextEditor(OpenFileDescriptor(project, it), true)
            }
        }
    }catch (e:Throwable){
        showErrorNotification(e, project)
    }
}

private fun onPushPull(command:Api.Command, project: Project, panel: PhraseToolWindowPanel){
    val yamlModel:YamlModel = try {
        YamlParser.read(project)
    }catch (e:PhraseConfigurationNotFoundException){
        onConfig(project); return
    }catch (e:Throwable){
        showErrorNotification(e, project); return
    }

    val api = Api(project, yamlModel.phrase.access_token)
    GlobalScope.launch(Dispatchers.Swing) {
        panel.clearLog()
        val callback = object : ApiCallback{
            override fun onMessage(message: String) { panel.append(message) }
            override fun onFinish() { panel.append(PhraseBundle.message("done")) }
        }
        try {
            api.runWithCallback(command, callback)
        }catch (e:Throwable){
            showErrorNotification(e, project)
        }
    }
}

private fun showErrorNotification(e:Throwable, project: Project){
    when(e) {
        is PhraseClientNotFoundException -> Notify.error(PhraseBundle.message("errorClientNotSpecified"), project, ActionOpenSettings())
        is PhraseConfigurationNotFoundException -> Notify.error(PhraseBundle.message("errorConfigurationNotFound"), project)
        is PhraseLoadConfigurationException -> Notify.error(PhraseBundle.message("errorParsingConfiguration"), project)
        is PhraseSaveConfigurationException -> Notify.error(PhraseBundle.message("errorSavingConfiguration"), project)
        else -> Notify.error(PhraseBundle.message("errorUnknown"), project)
    }
}
