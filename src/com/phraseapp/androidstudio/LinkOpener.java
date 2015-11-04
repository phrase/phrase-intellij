package com.phraseapp.androidstudio;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by kolja on 04.11.15.
 */
public class LinkOpener {

    public static void open(String url){
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException exc) {
                exc.printStackTrace();
            } catch (URISyntaxException exc) {
                exc.printStackTrace();
            }
        } else {
            Notifications.Bus.notify(new Notification("PhraseApp", "Error", "Could not locate browser, please head to " + url, NotificationType.ERROR));
        }
    }
}
