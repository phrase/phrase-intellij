package com.phraseapp.androidstudio.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.phraseapp.androidstudio.ActionEventHandler;

import javax.swing.*;

/**
 * Catches PullAction-Click from MainMenu
 */
public class PullAction extends AnAction {
    public PullAction() {
        super();
    }

    public PullAction(String text, String description, Icon icon) {
        super(text, description, icon);
    }

    public void actionPerformed(final AnActionEvent e) {
        ActionEventHandler handler = new ActionEventHandler();
        handler.handleEvent(e, "pull");
    }
}
