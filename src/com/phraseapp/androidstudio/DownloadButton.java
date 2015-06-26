package com.phraseapp.androidstudio;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Catches DownloadButton-Click from MainMenu
 */
public class DownloadButton extends AnAction {

    public void actionPerformed(final AnActionEvent e) {
        LinkedList<String> locales = API.getInstance().getLocales();
        final VirtualFile resDirectory = PhraseAppCommon.findResDirectory(e.getProject().getBaseDir());
        int numLocales = locales.size();
        int downloadErrors = 0;

        for(String localeName : locales){
            File localeFile = new File("");
            if (localeName.equals("en")) {
                localeFile = new File(resDirectory.getPath() + "/values/strings.xml");
            } else {
                localeFile = new File(resDirectory.getPath() + "/values-" + localeName + "/strings.xml");
            }
            try {
                FileUtils.writeStringToFile(localeFile, API.getInstance().downloadLocale(localeName), "UTF-8");
            } catch (IOException e1) {
                downloadErrors++;
            }
        }

        if (downloadErrors > 0){
            Notifications.Bus.notify(new Notification("PhraseApp", "Error", "Failed to save " + downloadErrors + " locale files.", NotificationType.ERROR));
        }else{
            Notifications.Bus.notify(new Notification("PhraseApp", "Success", "Saved " + numLocales + " locale files.", NotificationType.INFORMATION));
        }

    }
}
