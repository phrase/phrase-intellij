package com.phrase.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.phrase.intellij.ui.ColorTextPane;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by kolja on 28.10.15.
 */
public class ProjectLocalesUploader {
    private final Project project;
    private final String remoteProjectId;
    private final API api;
    private final ArrayList<String> remoteLocaleNames;
    private final LinkedList<VirtualFile> localLocales;

    public ProjectLocalesUploader(Project project, String remoteProjectId, API api){
        this.api = api;
        this.project = project;
        this.remoteProjectId = remoteProjectId;
        this.remoteLocaleNames = getRemoteLocaleNames(api);
        this.localLocales = ProjectHelper.findProjectLocales(ProjectUtil.guessProjectDir(project));

    }

    public boolean detectedMissingRemoteLocales(){
        for (int i = 0; i < localLocales.size(); i++) {
            if (!remoteLocaleNames.contains(ProjectHelper.getLocaleName(localLocales.get(i)))){
                return true;
            }
        }
        return false;
    }

    public void upload(){
        final ToolWindowOutputWriter outputWriter = new ToolWindowOutputWriter(project);
        outputWriter.writeOutput("Started Uploading missing locales ...");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                uploadLocales(api, localLocales, remoteLocaleNames, outputWriter);
                outputWriter.writeOutput("Finished");
            }
        });
        thread.start();
    }

    @NotNull
    private ArrayList<String> getRemoteLocaleNames(API api) {
        APIResourceListModel remoteLocales = api.getLocales(remoteProjectId);
        final ArrayList<String> remoteLocaleNames = new ArrayList<String>();
        for (int i = 0; i < remoteLocales.getSize(); i++) {
            remoteLocaleNames.add(remoteLocales.getModelAt(i).getName());
        }
        return remoteLocaleNames;
    }

    private void uploadLocales(API api, LinkedList<VirtualFile> localLocales, ArrayList<String> remoteLocaleNames, ToolWindowOutputWriter outputWriter) {
        for (VirtualFile locale : localLocales) {
            String localeName = ProjectHelper.getLocaleName(locale);
            final String relativePath = ProjectHelper.getRelativPath(project, locale);


            if (!remoteLocaleNames.contains(localeName)) {
                // Create Locale
                api.postLocales(
                        remoteProjectId,
                        localeName
                );
                // Upload Locale
                APIResourceListModel upload = api.uploadLocale(
                        remoteProjectId,
                        localeName,
                        locale.getPath(),
                        "xml",
                        "false");

                if (upload != null) {
                    if (!upload.isValid()) {
                        outputWriter.writeOutput("Could not upload locale: " + relativePath + "\n" + ColorTextPane.ANSI_RED + upload.getErrors() + ColorTextPane.ANSI_STOP);
                    } else {
                        outputWriter.writeOutput("Uploaded " + ColorTextPane.ANSI_GREEN + relativePath + ColorTextPane.ANSI_STOP);
                    }
                } else {
                    outputWriter.writeOutput("Could not upload locale: " + relativePath);
                }
            }
        }
    }
}
