package com.phrase.intellij

import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.phrase.intellij.ui.configurable.PhraseConfigurable
import java.awt.Desktop
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException

object Utils {

    fun findClientInstallation(): String? {
        return arrayOf(
            "/usr/bin/phrase",
            "/usr/local/bin/phrase",
            System.getProperty("user.home") + "/bin/phrase",
            System.getenv("ProgramFiles")+"\\Phrase\\phrase.exe",
            System.getenv("ProgramFiles(X86)")+"\\Phrase\\phrase.exe"
        ).firstOrNull { File(it).exists() }
    }

    fun openConfigurable(){
        ShowSettingsUtil.getInstance().showSettingsDialog(null, PhraseConfigurable::class.java)
    }

    fun openLink(url:String, project: Project?=null){
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(URI(url))
            } catch (e: IOException) {
                Notify.error("Could not locate browser, please head to $url", project)
            } catch (e: URISyntaxException) {
                Notify.error("Could not parse to URI $url", project)
            }
        } else {
            Notify.error("Could not locate browser, please head to $url", project)
        }
    }

}