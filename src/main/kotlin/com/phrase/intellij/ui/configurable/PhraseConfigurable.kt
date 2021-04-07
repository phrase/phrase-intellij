package com.phrase.intellij.ui.configurable

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.vfs.VirtualFile
import com.phrase.intellij.PhraseBundle
import com.phrase.intellij.PhrasePrefs
import com.phrase.intellij.PhraseUrls
import com.phrase.intellij.Utils
import com.phrase.intellij.ui.JHtmlPane
import com.phrase.intellij.ui.SimpleDocumentListener
import java.io.File
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel

class PhraseConfigurable:Configurable, Configurable.NoScroll {
    private lateinit var rootPanel:JPanel
    private lateinit var htmlPane:JHtmlPane
    private lateinit var clientPath: TextFieldWithBrowseButton
    private lateinit var fileNotFound: JLabel

    private var modified = false

    override fun createComponent(): JComponent {
        htmlPane.setHtml("<p>${PhraseBundle.message("pluginRequiresCli")} <a href=${PhraseUrls.HELP}>${PhraseBundle.message("learnMore")}</a>.</p>")

        clientPath.textField.document.addDocumentListener(SimpleDocumentListener{
            refreshNotFound()
            modified = true
        })

        clientPath.addBrowseFolderListener(PhraseBundle.message("chooseClientFile"), "", null, object:FileChooserDescriptor(true, false, false, false, false, false) {
            override fun isFileSelectable(file: VirtualFile) = file.name.startsWith("phrase")
        })

        return rootPanel
    }

    override fun reset() {
        modified = false
        val saved = PhrasePrefs.clientPath
        if(saved.isNullOrBlank()){
            val guess = Utils.findClientInstallation()
            clientPath.textField.text = guess
            if(guess!=null){
                JOptionPane.showMessageDialog(rootPanel, PhraseBundle.message("clientWasFoundAt").format(guess))
                modified = true
            }
        }else{
            clientPath.textField.text = saved
        }
        refreshNotFound()
    }

    private fun refreshNotFound(){
        val path = clientPath.text.trim()
        fileNotFound.isVisible = !(path.isEmpty() || File(path).exists())
    }

    override fun isModified() = modified

    override fun apply() {
        PhrasePrefs.clientPath = clientPath.text.trim()
        modified = false
    }

    override fun getDisplayName() = PhraseBundle.message("phrase")

    override fun getHelpTopic() = PhraseBundle.message("pluginRequiresCli")

}