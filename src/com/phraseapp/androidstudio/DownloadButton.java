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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Catches DownloadButton-Click from MainMenu
 */
public class DownloadButton extends AnAction {

    public void actionPerformed(final AnActionEvent e) {
        if (TokenRepository.getInstance().getToken() == null) {
            Notifications.Bus.notify(new Notification("Phrase App", "Result", "No token found! Please set token in settings.", NotificationType.ERROR));
            return;
        }

        LinkedList<String> remoteLocales = API.getInstance().getLocales();
        final VirtualFile resDirectory = PhraseAppCommon.findResDirectory(e.getProject().getBaseDir());

        final int numDownloads = remoteLocales.size();

        final CountDownLatch latch = new CountDownLatch(numDownloads);
        final AtomicInteger errorCount = new AtomicInteger();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();

                    // Display PopUp with Results of Downloadaction
                    if (numDownloads == 0) {
                        Notifications.Bus.notify(new Notification("Phrase App", "Result", "No locale where downloaded!", NotificationType.WARNING));
                    } else {
                        if (errorCount.get() > 0) {
                            Notifications.Bus.notify(new Notification("Phrase App", "Result", "Failed to download " + errorCount + " out of " + numDownloads + " locales!", NotificationType.WARNING));
                        } else {
                            Notifications.Bus.notify(new Notification("Phrase App", "Result", "Download of " + numDownloads + " locales was successful!", NotificationType.INFORMATION));
                        }
                    }

                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();

        for (String locale : remoteLocales) {
            final String remoteLocale = locale;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean success;
                    try {
                        success = downloadLocale(resDirectory, remoteLocale);
                    } catch (Throwable t) {
                        success = false;
                    }
                    if (success) {
                        Notifications.Bus.notify(new Notification("Phrase App", "Download succeeded", "Locale: " + remoteLocale, NotificationType.INFORMATION));
                    } else {
                        errorCount.incrementAndGet();
                        Notifications.Bus.notify(new Notification("Phrase App", "Download failed", "Locale: " + remoteLocale, NotificationType.INFORMATION));
                    }
                    latch.countDown();
                }
            }).start();
        }
    }

    private boolean downloadLocale(VirtualFile resDirectory, String remoteLocale) {
        File localeFile;

        // Build Path to localefile
        if (remoteLocale.equals("en")) {
            localeFile = new File(resDirectory.getPath() + "/values/strings.xml");
        } else if (remoteLocale.equals("id")) {
            localeFile = new File(resDirectory.getPath() + "/values-in/strings.xml");
        } else {
            localeFile = new File(resDirectory.getPath() + "/values-" + remoteLocale.replace("-", "-r") + "/strings.xml");
        }

        // Check if directory exists/create
        if (!localeFile.exists()) {
            localeFile.getParentFile().mkdir();
        }

        // Download and write file
        String content = API.getInstance().downloadLocaleFile(remoteLocale);
        if (content != null) {
            try {
                FileUtils.writeStringToFile(localeFile, content, "UTF-8");
                return true;
            } catch (IOException e1) {
                return false;
            }
        }
        return false;
    }
}
