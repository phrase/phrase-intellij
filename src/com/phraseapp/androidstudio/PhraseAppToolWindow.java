package com.phraseapp.androidstudio;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;

/**
 * Created by kolja on 12.10.15.
 */
public class PhraseAppToolWindow implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        toolWindow.setToHideOnEmptyContent(true);

        ColorTextPane textArea = new ColorTextPane();
        textArea.setEditable(false);
        textArea.setName("OutputTextArea");
        JScrollPane scrollPane = new JScrollPane(textArea);
        final Content content = ContentFactory.SERVICE.getInstance().createContent(scrollPane, "", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.activate(null);
    }
}
