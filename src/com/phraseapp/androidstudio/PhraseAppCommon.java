package com.phraseapp.androidstudio;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.vfs.VFileProperty;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.LinkedList;

public class PhraseAppCommon {


    /**
     * Find the ressource directory where locales are located
     *
     * @param baseDir
     * @return Path to ressource Directory
     */
    protected static VirtualFile findResDirectory(VirtualFile baseDir) {
        LinkedList<VirtualFile> fileList = new LinkedList<VirtualFile>();
        smartVisit(baseDir, fileList);
        for (VirtualFile file : fileList) {
            if (isLocaleFile(file)) {
                return file.getParent().getParent();
            }
        }
        return null;
    }


    /**
     * Get short localecode from given file
     *
     * @param file the locale file
     * @return short localecode, e.g. 'en'
     */
    protected static String getLocaleCode(VirtualFile file) {
        if (file.getParent().getName().contains("values-")) {
            String localecode[] = file.getParent().getName().split("-");
            return localecode.length == 3 ? localecode[1] + "-" + localecode[2] : localecode[1];
        } else {
            return "en";
        }
    }

    /**
     * Find all locales used in Projekt
     *
     * @return List with locale short codes, e.g. ['en', 'de', 'es', ...]
     */
    protected static LinkedList<String> findProjectLocales(VirtualFile resDir) {
        LinkedList<String> locales = new LinkedList<String>();
        LinkedList<VirtualFile> fileList = new LinkedList<VirtualFile>();
        smartVisit(resDir, fileList);
        for (VirtualFile file : fileList) {
            if (isLocaleFile(file)) {
                locales.add(getLocaleCode(file));
            }
        }
        return locales;
    }


    /**
     * Check if a file is a locale file
     *
     * @param file ProjectDir where we start searcing
     * @return true/false
     */
    protected static boolean isLocaleFile(VirtualFile file) {
        return file.getPath().contains("res/values") && file.getName().equals("strings.xml")
                && !file.getPath().contains("res/values-zz")
                && !file.getPath().contains("res/values-id");
    }


    /**
     * Recursivley visit all files in directory
     *
     * @param file     Base directory to start search
     * @param fileList List with all found files
     */
    protected static void smartVisit(VirtualFile file, LinkedList<VirtualFile> fileList) {
        if (file.isDirectory() && !file.getName().startsWith(".") &&
                !file.is(VFileProperty.SYMLINK) && file.getChildren() != null) {
            for (VirtualFile childFile : file.getChildren()) {
                smartVisit(childFile, fileList);
            }
        } else if (!file.isDirectory()) {
            fileList.add(file);
        }
    }

}
