package com.phraseapp.androidstudio;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.phraseapp.androidstudio.*;
import com.phraseapp.androidstudio.actions.ConfigAction;


/**
 * Created by kolja on 12.10.15.
 */
public class ActionEventHandler {
    public void handleEvent(final AnActionEvent e, final String clientAction) {
        final String clientPath = PropertiesRepository.getInstance().getClientPath();

        if (clientPath == null || clientPath.isEmpty() || !API.validateClient(clientPath)) {
            Notifications.Bus.notify(new Notification("PhraseApp", "Error", "Please choose a valid PhraseApp client in the PhraseApp plugin settings.", NotificationType.ERROR));
            return;
        }

        Project project = e.getProject();
        PhraseAppConfiguration configuration = new PhraseAppConfiguration(e.getProject());
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

        PushPullAdapter phraseAppClient = new PushPullAdapter(clientPath, project);
        phraseAppClient.run(clientAction);
    }
}
