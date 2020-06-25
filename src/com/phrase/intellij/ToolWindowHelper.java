package com.phrase.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.phrase.intellij.ui.ColorTextPane;
import com.phrase.intellij.ui.ToolWindowPane;
import org.jetbrains.annotations.Nullable;

/**
 * Created by kolja on 27.10.15.
 */
public class ToolWindowHelper {

    private final ToolWindow outputWindow;

    public ToolWindowHelper(Project project){
        outputWindow = ToolWindowManager.getInstance(project).getToolWindow("Phrase");
    }

    @Nullable
    public ColorTextPane getColorTextPane() {
        final Content content = outputWindow.getContentManager().getContent(0);

        if (content != null) {
            ToolWindowPane pane = (ToolWindowPane) content.getComponent();
            return pane.getOutputTextArea();

        }
        return null;
    }

    public ToolWindow getOutputWindow() {
        return outputWindow;
    }
}
