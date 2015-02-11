package com.phraseapp.androidstudio;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.vfs.VirtualFile;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;


/***
 * Catches UploadButton-Click from MainMenu
 */
public class UploadButton extends AnAction {

    public void actionPerformed(AnActionEvent e) {

        PhraseAppAPI api = new PhraseAppAPI();
        VirtualFile resDirectory = PhraseAppCommon.findResDirectory(e.getProject().getBaseDir());

        int uploadErrors = 0;
        int numUploads = PhraseAppCommon.findProjectLocales(resDirectory).size();

        for(String localecode : PhraseAppCommon.findProjectLocales(resDirectory)){
            File localeFile;

            // Build Path to localeFile
            if(localecode.equals("en")){
                localeFile = new File(resDirectory.getPath() + "/values/strings.xml");
            }else{
                localeFile = new File(resDirectory.getPath() + "/values-" + localecode + "/strings.xml");
            }

            // Upload
            try {
                if(!api.uploadLocaleFile(localecode, FileUtils.readFileToString(localeFile, "UTF-8"))){
                    uploadErrors++;
                }
            } catch (IOException e1) {
                uploadErrors++;
                e1.printStackTrace();
            }
        }

        // Display PopUp with Results of UploadAction
        if(numUploads == 0){
            PhraseAppCommon.showMessage("No locale files where found!", MessageType.WARNING, e);
        }else{
            if(uploadErrors > 0){
                PhraseAppCommon.showMessage("Failed to upload " + uploadErrors + " out of " + numUploads + " locales!", MessageType.ERROR, e);
            }else{
                PhraseAppCommon.showMessage("Upload of " + numUploads + " locales was successful!", MessageType.INFO, e);
            }
        }

    }
}
