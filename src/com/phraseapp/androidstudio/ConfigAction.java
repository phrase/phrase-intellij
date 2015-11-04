package com.phraseapp.androidstudio;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.phraseapp.androidstudio.ui.ProjectConfigDialog;

import javax.swing.*;

/**
 * Created by gfrey on 02/11/15.
 */
public class ConfigAction extends AnAction {
    public ConfigAction() {
    super();
}

    public ConfigAction(String text, String description, Icon icon) {
        super(text, description, icon);
    }

    public void actionPerformed(AnActionEvent e) {
        String clientPath = PropertiesRepository.getInstance().getClientPath();

        if (clientPath == null || clientPath.isEmpty() || !API.validateClient(clientPath)) {
            Notifications.Bus.notify(new Notification("PhraseApp", "Error", "Please configure the path to the PhraseApp client in the PhraseApp plugin settings.", NotificationType.ERROR));
            return;
        }

        ProjectConfigDialog dialog = new ProjectConfigDialog(e.getProject(), clientPath);

        dialog.show();
        if (dialog.getExitCode() == 0) {
            dialog.writeConfigFile();
        }
    }
}
