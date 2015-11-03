package com.phraseapp.androidstudio.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WebButton extends AnAction {

    public WebButton() {
    }

    public WebButton(String s, String s1, Icon icon) {
        super(s, s1, icon);
    }

    public void actionPerformed(AnActionEvent e) {

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI("https://phraseapp.com/projects"));
            } catch (IOException exc) {
                exc.printStackTrace();
            } catch (URISyntaxException exc) {
                exc.printStackTrace();
            }
        } else {
            Notifications.Bus.notify(new Notification("PhraseApp", "Error", "Could not locate browser, please head to https://phraseapp.com/", NotificationType.ERROR));
        }

    }


}
