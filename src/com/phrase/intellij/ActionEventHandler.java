package com.phrase.intellij;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.phrase.intellij.actions.ConfigAction;


/**
 * Created by kolja on 12.10.15.
 */
public class ActionEventHandler {
    public void handleEvent(final AnActionEvent e, final String clientAction) {
        final String clientPath = PropertiesRepository.getInstance().getClientPath();


        if (clientPath != null && !clientPath.isEmpty() && API.isLegacyClient(clientPath)) {
            Notifications.Bus.notify(new Notification("Phrase", "Error", "The client is no longer supported. Please upgrade to the new version https://github.com/phrase/phrase-cli.", NotificationType.ERROR));
            return;
        }

        if (clientPath == null || clientPath.isEmpty() || !API.validateClient(clientPath)) {
            Notifications.Bus.notify(new Notification("Phrase", "Error", "Please choose a valid Phrase client in the Phrase plugin settings.", NotificationType.ERROR));
            return;
        }


        Project project = e.getProject();
        PhraseConfiguration configuration = new PhraseConfiguration(e.getProject());
        if (!configuration.configExists()) {
            ConfigAction ca = new ConfigAction();
            ca.actionPerformed(e);
            // Validate configuration was properly written.
            configuration.loadPhraseConfig();
            if (!configuration.configExists()) {
                Notifications.Bus.notify(new Notification("Phrase", "Error", "No Phrase configuration found for current project..", NotificationType.ERROR));
                return;
            }
        }

        PushPullAdapter client = new PushPullAdapter(clientPath, project);
        client.run(clientAction);
    }
}
