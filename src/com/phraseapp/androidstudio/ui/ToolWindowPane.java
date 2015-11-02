package com.phraseapp.androidstudio.ui;

import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;

/**
 * Created by gfrey on 02/11/15.
 */
public class ToolWindowPane extends SimpleToolWindowPanel {
    private ColorTextPane textArea;

    public ToolWindowPane(boolean vertical, boolean borderless) {
        super(vertical, borderless);
    }

    public ToolWindowPane(boolean vertical) {
        super(vertical);
    }

    public void setOutputTextArea(ColorTextPane textArea) {
        this.textArea = textArea;
        this.textArea.setEditable(false);
        this.textArea.setName("OutputTextArea");

        JScrollPane scrollPane = new JBScrollPane(this.textArea);
        this.add(scrollPane);
    }

    public ColorTextPane getOutputTextArea() {
        return textArea;
    }
}
