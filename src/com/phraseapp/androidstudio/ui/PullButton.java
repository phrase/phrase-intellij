package com.phraseapp.androidstudio.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Catches PullButton-Click from MainMenu
 */
public class PullButton extends AnAction {

    public void actionPerformed(final AnActionEvent e) {
        ButtonEventHandler handler = new ButtonEventHandler();
        handler.handleEvent(e, "pull");
    }
}
