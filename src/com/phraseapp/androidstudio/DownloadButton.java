package com.phraseapp.androidstudio;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/***
 * Catches DownloadButton-Click from MainMenu
 */
public class DownloadButton extends AnAction {

    public void actionPerformed(AnActionEvent e) {

        PhraseAppAPI api = new PhraseAppAPI();
        LinkedList<String> locales = api.getLocales();
        VirtualFile resDirectory = PhraseAppCommon.findResDirectory(e.getProject().getBaseDir());

        int downloadErrors = 0;
        int numDownloads = locales.size();

        for(String localecode : locales){
            File localeFile;

            // Build Path to localefile
            if(localecode.equals("en")){
                localeFile = new File(resDirectory.getPath() + "/values/strings.xml");
            }else{
                localeFile = new File(resDirectory.getPath() + "/values-" + localecode + "/strings.xml");
            }

            // Check if directory exists/create
            if(!localeFile.exists()){
                localeFile.getParentFile().mkdir();
            }

            // Download and write file
            String content = api.downloadLocaleFile(localecode);
            if(content != null){
                try {
                    FileUtils.writeStringToFile(localeFile, content, "UTF-8");
                } catch (IOException e1) {
                    downloadErrors++;
                    e1.printStackTrace();
                }
            }else{
                downloadErrors++;
            }

            // Display PopUp with Results of Downloadaction
            if(numDownloads == 0){
                PhraseAppCommon.showMessage("No locale where downloaded!", MessageType.WARNING, e);
            }else{
                if(downloadErrors > 0){
                    PhraseAppCommon.showMessage("Failed to download " + downloadErrors + " out of " + numDownloads + " locales!", MessageType.ERROR, e);
                }else{
                    PhraseAppCommon.showMessage("Download of " + numDownloads + " locales was successful!", MessageType.INFO, e);
                }
            }

        }

    }
}
