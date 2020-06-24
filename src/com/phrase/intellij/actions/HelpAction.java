package com.phrase.intellij.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.phrase.intellij.LinkOpener;

import javax.swing.*;

/**
 * Created by gfrey on 02/11/15.
 */
public class HelpAction extends AnAction {
    public HelpAction() {
        super();
    }

    public HelpAction(String text, String description, Icon icon) {
        super(text, description, icon);
    }
    public void actionPerformed(AnActionEvent e) {
        LinkOpener.open("https://help.phrase.com/setup/set-up-phrase-for-app-translation/android-android-studio");
    }


}
