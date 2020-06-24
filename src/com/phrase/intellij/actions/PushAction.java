package com.phrase.intellij.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.phrase.intellij.ActionEventHandler;

import javax.swing.*;

/**
 * Catches PushAction-Click from MainMenu
 */
public class PushAction extends AnAction {
    public PushAction() {
        super();
    }

    public PushAction(String text, String description, Icon icon) {
        super(text, description, icon);
    }

    public final void actionPerformed(final AnActionEvent e) {
        ActionEventHandler handler = new ActionEventHandler();
        handler.handleEvent(e, "push");
    }
}

