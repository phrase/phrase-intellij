package com.phrase.intellij.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.phrase.intellij.API;
import com.phrase.intellij.PropertiesRepository;
import com.phrase.intellij.ui.ProjectConfigDialog;

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
            Notifications.Bus.notify(new Notification("Phrase", "Error", "Please configure the path to the Phrase client in the Phrase plugin settings.", NotificationType.ERROR));
            return;
        }

        ProjectConfigDialog dialog = new ProjectConfigDialog(e.getProject(), clientPath);

        dialog.show();
        if (dialog.getExitCode() == 0) {
            dialog.writeConfigFile();
        }
    }
}
