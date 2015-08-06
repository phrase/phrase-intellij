package com.phraseapp.androidstudio;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.VirtualFile;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Catches UploadButton-Click from MainMenu
 */
public class UploadButton extends AnAction {


    public void actionPerformed(final AnActionEvent e) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                final VirtualFile resDirectory = PhraseAppCommon.findResDirectory(e.getProject().getBaseDir());
                LinkedList<String> projectLocales = PhraseAppCommon.findProjectLocales(resDirectory);
                int numLocales = projectLocales.size();

                int uploadErrors = 0;

                for(String localeName : projectLocales){
                    String localeFile = "";
                    if (localeName.equals("en")) {
                        localeFile = resDirectory.getPath() + "/values/strings.xml";
                    } else {
                        localeFile = resDirectory.getPath() + "/values-" + localeName + "/strings.xml";
                    }

                    if(!API.getInstance().uploadLocale(localeFile, localeName)){
                        uploadErrors++;
                        Notifications.Bus.notify(new Notification("PhraseApp", "Error", "Failed to upload locale " + localeName + " at " + localeFile, NotificationType.ERROR));
                    }
                }

                if (uploadErrors > 0){
                    Notifications.Bus.notify(new Notification("PhraseApp", "Error", "Failed to upload " + uploadErrors + " locale files.", NotificationType.ERROR));
                }else{
                    Notifications.Bus.notify(new Notification("PhraseApp", "Success", "Uploaded " + numLocales + " locale files.", NotificationType.INFORMATION));
                }
            }
        }).start();


    }

}
