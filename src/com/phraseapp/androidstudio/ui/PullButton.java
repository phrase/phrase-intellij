package com.phraseapp.androidstudio.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import javax.swing.*;

/**
 * Catches PullButton-Click from MainMenu
 */
public class PullButton extends AnAction {
    public PullButton() {
        super();
    }

    public PullButton(String text, String description, Icon icon) {
        super(text, description, icon);
    }

    public void actionPerformed(final AnActionEvent e) {
        ButtonEventHandler handler = new ButtonEventHandler();
        handler.handleEvent(e, "pull");
    }
}
