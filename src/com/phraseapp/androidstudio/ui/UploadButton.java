package com.phraseapp.androidstudio.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.phraseapp.androidstudio.*;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        final PhraseAppConfiguration configuration = new PhraseAppConfiguration(e.getProject());
        if (!configuration.configExists()) {
            Notifications.Bus.notify(new Notification("PhraseApp", "Error", "A .phraseapp.yml could not be found. Please use the PhraseApp plugin settings to generate one.", NotificationType.ERROR));
            return;
        }

        if (!configuration.hasProjectId() || !configuration.hasAccessToken()) {
            Notifications.Bus.notify(new Notification("PhraseApp", "Error", "Please verify that your .phraseapp.yml contains a valid access_token and project_id.", NotificationType.ERROR));
            return;
        }

        final VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        final String localeName = ProjectHelper.getLocaleName(file);
        final ToolWindowOutputWriter outputWriter = new ToolWindowOutputWriter(e.getProject());
        outputWriter.writeOutput("Started Uploading locale: " + ColorTextPane.ANSI_GREEN + localeName + ColorTextPane.ANSI_STOP);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final API api = new API(PropertiesRepository.getInstance().getClientPath(), configuration.getAccessToken(), e.getProject());
                api.postLocales(configuration.getProjectId(), localeName);
                APIResourceListModel upload = api.uploadLocale(
                        configuration.getProjectId(),
                        ProjectHelper.getLocaleName(file),
                        file.getPath(),
                        "xml"
                );

                if (upload != null && !upload.isValid()) {
                    outputWriter.writeOutput(ColorTextPane.ANSI_RED + upload.getErrors() + ColorTextPane.ANSI_STOP);
                }

                outputWriter.writeOutput("Finished");
            }
        });

        thread.start();

    }

}
