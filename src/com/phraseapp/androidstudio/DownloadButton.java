package com.phraseapp.androidstudio;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Catches DownloadButton-Click from MainMenu
 */
public class DownloadButton extends AnAction {

    public void actionPerformed(final AnActionEvent e) {
        ButtonEventHandler handler = new ButtonEventHandler();
        handler.handleEvent(e, "pull");
    }
}
