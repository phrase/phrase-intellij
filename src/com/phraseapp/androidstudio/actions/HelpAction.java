package com.phraseapp.androidstudio.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.phraseapp.androidstudio.LinkOpener;

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
        LinkOpener.open("http://docs.phraseapp.com/guides/setup/android/");
    }


}
