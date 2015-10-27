package com.phraseapp.androidstudio;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.phraseapp.androidstudio.ui.ColorTextPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Created by kolja on 27.10.15.
 */
public class ToolWindowHelper {

    private final ToolWindow outputWindow;

    public ToolWindowHelper(Project project){
        outputWindow = ToolWindowManager.getInstance(project).getToolWindow("PhraseApp");
    }

    @Nullable
    public ColorTextPane getColorTextPane() {
        final Content content = outputWindow.getContentManager().getContent(0);
        ColorTextPane area = null;
        if (content != null) {

            JScrollPane pane = (JScrollPane) content.getComponent();
            JViewport viewport = pane.getViewport();
            Component[] components = viewport.getComponents();
            for (int i = 0; i < components.length; i++) {

                if (components[i].getClass().getName().toString().equals("com.phraseapp.androidstudio.ui.ColorTextPane")) {
                    area = (ColorTextPane) components[i];
                }
            }
        }
        return area;
    }

    public ToolWindow getOutputWindow() {
        return outputWindow;
    }
}
