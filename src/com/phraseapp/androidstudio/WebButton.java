package com.phraseapp.androidstudio;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WebButton extends AnAction {

    public void actionPerformed(AnActionEvent e) {

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI("https://phraseapp.com/projects"));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
        } else {
            Notifications.Bus.notify(new Notification("Phrase App", "Error", "Could not locate browser, please head to https://phraseapp.com/", NotificationType.ERROR));
        }

    }


}
