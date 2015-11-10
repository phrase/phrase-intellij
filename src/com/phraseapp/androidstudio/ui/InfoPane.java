package com.phraseapp.androidstudio.ui;

import com.phraseapp.androidstudio.LinkOpener;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.IOException;

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
            LinkOpener.open(event.getURL().toString());
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
