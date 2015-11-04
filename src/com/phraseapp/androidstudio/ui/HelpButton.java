package com.phraseapp.androidstudio.ui;

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

/**
 * Created by gfrey on 02/11/15.
 */
public class HelpButton extends AnAction {
    public HelpButton() {
        super();
    }

    public HelpButton(String text, String description, Icon icon) {
        super(text, description, icon);
    }
    public void actionPerformed(AnActionEvent e) {
        LinkOpener.open("http://docs.phraseapp.com/guides/setup/android/");
    }


}
