package com.phraseapp.androidstudio;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.Charset;

/**
 * Created by kolja on 15.10.15.
 */
public class PhraseAppClient {
    private String clientPath;
    private String projectPath;
    private ToolWindow outputWindow;

    public PhraseAppClient(final String path, Project project){
        clientPath = path;
        projectPath = project.getBasePath();
        outputWindow = ToolWindowManager.getInstance(project).getToolWindow("PhraseApp");
    }

    public void run(final String clientAction) {
        if (outputWindow.isActive()) {
            runCommand(clientAction);
        } else {
            outputWindow.activate(new Runnable() {
                @Override
                public void run() {
                    runCommand(clientAction);

                }
            });
        }
    }

    private void runCommand(final String clientAction) {
        final Content content = outputWindow.getContentManager().getContent(0);
        ColorTextPane area = null;
        if (content != null) {

            JScrollPane pane = (JScrollPane) content.getComponent();
            JViewport viewport = pane.getViewport();
            Component[] components = viewport.getComponents();
            for (int i = 0; i < components.length; i++) {

                if (components[i].getClass().getName().toString().equals("com.phraseapp.androidstudio.ColorTextPane")) {
                    area = (ColorTextPane) components[i];
                    area.setEditable(true);
                    area.setText("");
                    area.append(Color.getHSBColor(0.000f, 0.000f, 0.000f), "Connecting with PhraseApp ...\n");
                    area.setEditable(false);
                }
            }
        }

        final ColorTextPane finalArea = area;
        outputWindow.show(new Runnable() {
            @Override
            public void run() {
                try {
                    GeneralCommandLine gcl = new GeneralCommandLine(clientPath,
                            clientAction);
                    gcl.withWorkDirectory(projectPath);
                    ProcessOutput output = new CapturingProcessHandler(gcl.createProcess(), Charset.defaultCharset(), gcl.getCommandLineString()).runProcess();
                    String outputString = output.getStdout();
                    String errorString = output.getStderr();

                    if (finalArea != null) {
                        finalArea.setEditable(true);
                        finalArea.setText("");
                        finalArea.appendANSI(outputString);
                        finalArea.append(Color.getHSBColor(0.000f, 1.000f, 0.502f), errorString);
                        finalArea.setEditable(false);
                    }
                } catch (ExecutionException exception) {
                    Notifications.Bus.notify(new Notification("PhraseApp", "Error", exception.getMessage(), NotificationType.ERROR));
                }
            }
        });
    }
}
