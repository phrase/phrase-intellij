package com.phraseapp.androidstudio.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.phraseapp.androidstudio.ClientAdapter;
import com.phraseapp.androidstudio.PhraseAppConfiguration;
import com.phraseapp.androidstudio.PropertiesRepository;


/**
 * Created by kolja on 12.10.15.
 */
public class ButtonEventHandler {
    public void handleEvent(final AnActionEvent e, final String clientAction) {
        Project project = e.getProject();
        PhraseAppConfiguration configuration = new PhraseAppConfiguration(e.getProject());
        final String clientPath = PropertiesRepository.getInstance().getClientPath();

        if (clientPath.isEmpty()) {
            Notifications.Bus.notify(new Notification("PhraseApp", "Error", "Please choose the 'phraseapp' client you want to use in the plugin settings.", NotificationType.ERROR));
            return;
        }

        if (!configuration.loadPhraseAppConfig().startsWith("phraseapp")) {
            Notifications.Bus.notify(new Notification("PhraseApp", "Error", "Please create a '.phraseapp.yml' configuration file in your project root folder. You can generate a '.phraseapp.yml' configuration file in the plugin settings.", NotificationType.ERROR));
            return;
        }

        ClientAdapter phraseAppClient = new ClientAdapter(clientPath, project);
        phraseAppClient.run(clientAction);
    }
}
