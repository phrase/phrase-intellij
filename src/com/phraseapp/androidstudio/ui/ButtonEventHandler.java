package com.phraseapp.androidstudio.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.phraseapp.androidstudio.*;


/**
 * Created by kolja on 12.10.15.
 */
public class ButtonEventHandler {
    public void handleEvent(final AnActionEvent e, final String clientAction) {
        final String clientPath = PropertiesRepository.getInstance().getClientPath();

        if (clientPath.isEmpty() || !API.validateClient(clientPath)) {
            Notifications.Bus.notify(new Notification("PhraseApp", "Error", "Please choose the 'phraseapp' client in the PhraseApp plugin settings.", NotificationType.ERROR));
            return;
        }

        Project project = e.getProject();
        PhraseAppConfiguration configuration = new PhraseAppConfiguration(e.getProject());
        if (!configuration.configExists()) {
            ConfigAction ca = new ConfigAction();
            ca.actionPerformed(e);
            return; // TODO maybe get response config
        }

        PushPullAdapter phraseAppClient = new PushPullAdapter(clientPath, project);
        phraseAppClient.run(clientAction);
    }
}
