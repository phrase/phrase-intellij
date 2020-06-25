package com.phrase.intellij.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.phrase.intellij.LinkOpener;

import javax.swing.*;


public class WebAction extends AnAction {

    public WebAction() {
    }

    public WebAction(String s, String s1, Icon icon) {
        super(s, s1, icon);
    }

    public void actionPerformed(AnActionEvent e) {
        LinkOpener.open("https://app.phrase.com/projects");
    }


}
