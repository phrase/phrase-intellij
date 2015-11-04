package com.phraseapp.androidstudio.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.phraseapp.androidstudio.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by kolja on 26.10.15.
 */
public class UploadButton extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        final VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        final boolean isStringsXML = isStringsFile(file);
        e.getPresentation().setEnabled(isStringsXML);
        e.getPresentation().setVisible(isStringsXML);
    }

    private static boolean isStringsFile(@Nullable VirtualFile file) {
        return file != null && file.getName().equals("strings.xml") && !ProjectHelper.getLocaleName(file).isEmpty();
    }

    @Override
    public void actionPerformed(final AnActionEvent e) {
        final String clientPath = PropertiesRepository.getInstance().getClientPath();
        if (clientPath == null || clientPath.isEmpty()) {
            Notifications.Bus.notify(new Notification("PhraseApp", "Error", "Please choose the PhraseApp Client in the PhraseApp plugin settings.", NotificationType.ERROR));
            return;
        }

        final PhraseAppConfiguration configuration = new PhraseAppConfiguration(e.getProject());
        if (!configuration.configExists()) {
            ConfigAction ca = new ConfigAction();
            ca.actionPerformed(e);
            // Validate configuration was properly written.
            configuration.loadPhraseAppConfig();
            if (!configuration.configExists()) {
                Notifications.Bus.notify(new Notification("PhraseApp", "Error", "No PhraseApp configuration found for current project..", NotificationType.ERROR));
                return;
            }
        }

        int choice = JOptionPane.showOptionDialog(null,
                "Do you want to update the translations existing in PhraseApp with the content of your file?",
                "PhraseApp",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, null, null);
        final String updateTranslations;
        if (choice == JOptionPane.YES_OPTION) {
            updateTranslations = "true";
        } else {
            updateTranslations = "false";
        }

        final VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        final String localeName = ProjectHelper.getLocaleName(file);
        final ToolWindowOutputWriter outputWriter = new ToolWindowOutputWriter(e.getProject());
        final String relativePath = ProjectHelper.getRelativPath(e.getProject(), file);

        outputWriter.writeOutput("Uploading " + ColorTextPane.ANSI_GREEN + relativePath + ColorTextPane.ANSI_STOP);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final API api = new API(PropertiesRepository.getInstance().getClientPath(), configuration.getAccessToken(), e.getProject());
                api.postLocales(configuration.getProjectId(), localeName);
                APIResourceListModel upload = api.uploadLocale(
                        configuration.getProjectId(),
                        ProjectHelper.getLocaleName(file),
                        file.getPath(),
                        "xml",
                        updateTranslations
                );

                if (upload != null && !upload.isValid()) {
                    outputWriter.writeOutput(ColorTextPane.ANSI_RED + upload.getErrors() + ColorTextPane.ANSI_STOP);
                }

                outputWriter.writeOutput("Uploaded " + ColorTextPane.ANSI_GREEN + relativePath + ColorTextPane.ANSI_STOP);
            }
        });

        thread.start();

    }
}
