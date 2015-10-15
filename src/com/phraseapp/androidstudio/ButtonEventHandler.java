package com.phraseapp.androidstudio;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;


/**
 * Created by kolja on 12.10.15.
 */
public class ButtonEventHandler {
    public void handleEvent(final AnActionEvent e, final String clientAction) {
        Project project = e.getProject();
        TokenRepository token_repo = TokenRepository.getInstance();
        final String clientPath = token_repo.getClientPath();

        if (clientPath.isEmpty()) {
            Notifications.Bus.notify(new Notification("PhraseApp", "Error", "Please choose the 'phraseapp' client you want to use in the plugin settings.", NotificationType.ERROR));
            return;
        }

        if (token_repo.loadPhraseAppConfig() == null) {
            Notifications.Bus.notify(new Notification("PhraseApp", "Error", "Please create a '.phraseapp.yml' configuration file in your project root folder. You can generate a '.phraseapp.yml' configuration file in the plugin settings.", NotificationType.ERROR));
            return;
        }

        PhraseAppClient phraseAppClient = new PhraseAppClient(clientPath, project);
        phraseAppClient.run(clientAction);
    }
}
