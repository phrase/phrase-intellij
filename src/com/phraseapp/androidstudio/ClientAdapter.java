package com.phraseapp.androidstudio;

import com.apple.eawt.Application;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kolja on 15.10.15.
 */
public class ClientAdapter {
    private String clientPath;
    private String projectPath;
    private ToolWindow outputWindow;
    private SimpleDateFormat sdf;

    public ClientAdapter(final String path, Project project) {
        clientPath = path;
        projectPath = project.getBasePath();
        outputWindow = ToolWindowManager.getInstance(project).getToolWindow("PhraseApp");
        sdf = new SimpleDateFormat("HH:mm:ss");
    }

    public void run(final String clientAction) {

        if (outputWindow.isActive()) {
            final ColorTextPane finalArea = getColorTextPane();
            outputWindow.show(new Runnable() {
                @Override
                public void run() {
                    runCommand(clientAction, finalArea);
                }
            });
        } else {
            outputWindow.activate(new Runnable() {
                @Override
                public void run() {
                    runCommand(clientAction, getColorTextPane());
                }
            });
        }
    }

    private void runCommand(final String clientAction, final ColorTextPane finalArea) {
        try {
            GeneralCommandLine gcl = new GeneralCommandLine(clientPath,
                    clientAction);
            gcl.withWorkDirectory(projectPath);
            final CapturingProcessHandler processHandler = new CapturingProcessHandler(gcl.createProcess(), Charset.defaultCharset(), gcl.getCommandLineString());
            processHandler.addProcessListener(new ProcessListener() {
                @Override
                public void startNotified(ProcessEvent event) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            finalArea.setEditable(true);
                            finalArea.appendANSI(getFormattedTime() + "Connecting with PhraseApp ...\n");
                        }
                    });
                }

                @Override
                public void processTerminated(ProcessEvent event) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            finalArea.append(Color.getHSBColor(0.000f, 0.000f, 0.000f), getFormattedTime() + "Finished\n");
                            finalArea.setEditable(false);
                        }
                    });
                }

                @Override
                public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {

                }

                @Override
                public void onTextAvailable(final ProcessEvent event, final Key outputType) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (event.getText().length() < 5) {
                                finalArea.appendANSI(event.getText());
                                return;
                            }

                            if (outputType.toString() == "stdout") {
                                finalArea.appendANSI(getFormattedTime() + event.getText());
                            } else if (outputType.toString() == "system") {
                                finalArea.append(Color.getHSBColor(0.000f, 0.000f, 0.000f), getFormattedTime() + event.getText());
                            } else {
                                finalArea.append(Color.getHSBColor(0.000f, 1.000f, 0.502f), event.getText());
                            }
                        }
                    });
                }
            });
            Thread queryThread = new Thread() {
                public void run() {
                    processHandler.runProcess();
                }
            };
            queryThread.start();

        } catch (ExecutionException exception) {
            Notifications.Bus.notify(new Notification("PhraseApp", "Error", exception.getMessage(), NotificationType.ERROR));
        }
    }

    @NotNull
    private String getFormattedTime() {
        return sdf.format(new Date()) + " ";
    }

    @Nullable
    private ColorTextPane getColorTextPane() {
        final Content content = outputWindow.getContentManager().getContent(0);
        ColorTextPane area = null;
        if (content != null) {

            JScrollPane pane = (JScrollPane) content.getComponent();
            JViewport viewport = pane.getViewport();
            Component[] components = viewport.getComponents();
            for (int i = 0; i < components.length; i++) {

                if (components[i].getClass().getName().toString().equals("com.phraseapp.androidstudio.ColorTextPane")) {
                    area = (ColorTextPane) components[i];
                }
            }
        }
        return area;
    }
}
