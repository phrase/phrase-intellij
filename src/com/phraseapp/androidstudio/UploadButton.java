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
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Catches UploadButton-Click from MainMenu
 */
public class UploadButton extends AnAction {
    private final boolean forceUpload;

    public UploadButton() {
        this(false);
    }

    public UploadButton(boolean forceUpload) {
        this.forceUpload = forceUpload;
    }

    public void actionPerformed(final AnActionEvent e) {
        if (TokenRepository.getInstance().getToken() == null) {
            Notifications.Bus.notify(new Notification("Phrase App", "Result", "No token found! Please set token in settings.", NotificationType.ERROR));
            return;
        }


        final VirtualFile resDirectory = PhraseAppCommon.findResDirectory(e.getProject().getBaseDir());

        Collection<String> projectLocales = PhraseAppCommon.findProjectLocales(resDirectory);

        final int numUploads = projectLocales.size();

        LinkedList<String> projectLocales1 = PhraseAppCommon.findProjectLocales(resDirectory);
        projectLocales1.removeAll(API.getInstance().getLocales());

        final CountDownLatch latch = new CountDownLatch(numUploads);
        final AtomicInteger errorCount = new AtomicInteger();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                    if (numUploads == 0) {
                        Notifications.Bus.notify(new Notification("Phrase App", "Result", "No locale files where found!", NotificationType.WARNING));
                    } else {
                        if (errorCount.get() > 0) {
                            Notifications.Bus.notify(new Notification("Phrase App", "Result", "Failed to upload " + errorCount.get() + " out of " + numUploads + " locales!", NotificationType.WARNING));
                        } else {
                            Notifications.Bus.notify(new Notification("Phrase App", "Result", "Upload of " + numUploads + " locales was successful!", NotificationType.INFORMATION));
                        }
                    }
                } catch (InterruptedException e) {
                    assert false : "It shouldn't happen";
                }
            }
        }).start();


        for (String locale : projectLocales) {
            final String localLocale = locale;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean success;
                    try {
                        success = uploadLocale(localLocale, resDirectory);
                    } catch (Throwable t) {
                        success = false;
                    }
                    if (success) {
                        Notifications.Bus.notify(new Notification("Phrase App", "Upload succeeded", "Locale: " + localLocale, NotificationType.INFORMATION));
                    } else {
                        errorCount.incrementAndGet();
                        Notifications.Bus.notify(new Notification("Phrase App", "Upload failed", "Locale: " + localLocale, NotificationType.INFORMATION));
                    }
                    latch.countDown();
                }
            }).start();
        }

    }

    private boolean uploadLocale(String localLocale, VirtualFile resDirectory) {
        File localeFile;

        // Build Path to localeFile
        if (localLocale.equals("en")) {
            localeFile = new File(resDirectory.getPath() + "/values/strings.xml");
        } else {
            localeFile = new File(resDirectory.getPath() + "/values-" + localLocale + "/strings.xml");
        }

        // Upload
        try {
            String remoteLocale = localLocale.equals("in") ? "id" : localLocale.replace("-r", "-");
            return API.getInstance().uploadLocaleFile(remoteLocale, FileUtils.readFileToString(localeFile, "UTF-8"), forceUpload);
        } catch (IOException e1) {
            return false;
        }
    }
}
