package com.phraseapp.androidstudio.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import javax.swing.*;

/**
 * Catches PushButton-Click from MainMenu
 */
public class PushButton extends AnAction {
    public PushButton() {
        super();
    }

    public PushButton(String text, String description, Icon icon) {
        super(text, description, icon);
    }

    public final void actionPerformed(final AnActionEvent e) {
        ButtonEventHandler handler = new ButtonEventHandler();
        handler.handleEvent(e, "push");
    }
}

