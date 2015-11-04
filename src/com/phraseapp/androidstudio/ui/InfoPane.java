package com.phraseapp.androidstudio.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by gfrey on 04/11/15.
 */
public class InfoPane extends JTextPane {
    public void initializeActions() {
        this.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent event) {
                handleLinkClick(event);
            }
        });
    }

    private void handleLinkClick(HyperlinkEvent event) {
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                Desktop.getDesktop().browse(event.getURL().toURI());
            } catch (IOException e) {
                Notifications.Bus.notify(new Notification("PhraseApp", "Error", "Could not locate browser, please head to " + event.getURL().toString(), NotificationType.ERROR));
            } catch (URISyntaxException e) {
                Notifications.Bus.notify(new Notification("PhraseApp", "Error", "Could not parse to URI " + event.getURL().toString(), NotificationType.ERROR));
            }
        }
    }

    public void setContent(String text) {
        this.setContentType("text/html");
        HTMLDocument doc = (HTMLDocument) this.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit) this.getEditorKit();
        try {
            editorKit.insertHTML(doc, doc.getLength(), text, 0, 0, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
