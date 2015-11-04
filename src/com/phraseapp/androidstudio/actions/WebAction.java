package com.phraseapp.androidstudio.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.phraseapp.androidstudio.LinkOpener;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WebAction extends AnAction {

    public WebAction() {
    }

    public WebAction(String s, String s1, Icon icon) {
        super(s, s1, icon);
    }

    public void actionPerformed(AnActionEvent e) {
        LinkOpener.open("https://phraseapp.com/projects");
    }


}
