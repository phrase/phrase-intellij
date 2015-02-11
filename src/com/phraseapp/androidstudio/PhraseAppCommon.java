package com.phraseapp.androidstudio;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VFileProperty;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;

import java.util.LinkedList;

public class PhraseAppCommon {

    /**
     * Show a PopUp Message
     * @param message_value Text to display
     * @param message_type Type of Message (ERROR, WARNING, INFO)
     * @param event Context
     */
    protected static void showMessage(String message_value, MessageType message_type, AnActionEvent event){
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(DataKeys.PROJECT.getData(event.getDataContext()));
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(message_value, message_type, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
    }


    /***
     * Find the ressource directory where locales are located
     * @param baseDir
     * @return Path to ressource Directory
     */
    protected static VirtualFile findResDirectory(VirtualFile baseDir){
        LinkedList<VirtualFile> fileList = new LinkedList<VirtualFile>();
        smartVisit(baseDir, fileList);
        for(VirtualFile file : fileList){
            if(isLocaleFile(file)){
                return file.getParent().getParent();
            }
        }
        return null;
    }


    /***
     * Get short localecode from given file
     * @param file the locale file
     * @return short localecode, e.g. 'en'
     */
    protected static String getLocaleCode(VirtualFile file){
        if(file.getParent().getName().contains("values-")){
            String localecode[] = file.getParent().getName().split("-");
            return localecode[1];
        }else{
            return "en";
        }
    }

    /***
     * Find all locales used in Projekt
     * @return List with locale short codes, e.g. ['en', 'de', 'es', ...]
     */
    protected static LinkedList<String> findProjectLocales(VirtualFile resDir){
        LinkedList<String> locales = new LinkedList<String>();
        LinkedList<VirtualFile> fileList = new LinkedList<VirtualFile>();
        smartVisit(resDir, fileList);
        for(VirtualFile file : fileList){
            if(isLocaleFile(file)){
                locales.add(getLocaleCode(file));
            }
        }
        return locales;
    }


    /***
     * Check if a file is a locale file
     * @param file ProjectDir where we start searcing
     * @return true/false
     */
    protected static boolean isLocaleFile(VirtualFile file){
        return (file.getPath().contains("res/values") && file.getName().equals("strings.xml"));
    }


    /***
     * Recursivley visit all files in directory
     * @param file Base directory to start search
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
