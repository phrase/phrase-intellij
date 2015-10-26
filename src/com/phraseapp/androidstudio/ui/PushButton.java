package com.phraseapp.androidstudio.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Catches PushButton-Click from MainMenu
 */
public class PushButton extends AnAction {

    public final void actionPerformed(final AnActionEvent e) {
        ButtonEventHandler handler = new ButtonEventHandler();
        handler.handleEvent(e, "push");
    }
}

