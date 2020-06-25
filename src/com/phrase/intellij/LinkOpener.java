package com.phrase.intellij;

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
            } catch (IOException e) {
                Notifications.Bus.notify(new Notification("Phrase", "Error", "Could not locate browser, please head to " + url, NotificationType.ERROR));
            } catch (URISyntaxException e) {
                Notifications.Bus.notify(new Notification("Phrase", "Error", "Could not parse to URI " + url, NotificationType.ERROR));
            }
        } else {
            Notifications.Bus.notify(new Notification("Phrase", "Error", "Could not locate browser, please head to " + url, NotificationType.ERROR));
        }
    }
}
