package com.phraseapp.androidstudio;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.phraseapp.androidstudio.ui.ColorTextPane;
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
public class PushPullAdapter {
    private String clientPath;
    private String projectPath;
    private ToolWindowHelper outputWindowHelper;
    private SimpleDateFormat sdf;

    public PushPullAdapter(final String path, Project project) {
        clientPath = path;
        projectPath = project.getBasePath();
        outputWindowHelper = new ToolWindowHelper(project);
        sdf = new SimpleDateFormat("HH:mm:ss");
    }

    public void run(final String clientAction) {

        if (outputWindowHelper.getOutputWindow().isActive()) {
            final ColorTextPane finalArea = outputWindowHelper.getColorTextPane();
            outputWindowHelper.getOutputWindow().show(new Runnable() {
                @Override
                public void run() {
                    runCommand(clientAction, finalArea);
                }
            });
        } else {
            outputWindowHelper.getOutputWindow().activate(new Runnable() {
                @Override
                public void run() {
                    runCommand(clientAction, outputWindowHelper.getColorTextPane());
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
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            finalArea.setEditable(true);
                            finalArea.appendANSI(getFormattedTime() + "Connecting with PhraseApp ...\n");
                        }
                    });
                }

                @Override
                public void processTerminated(ProcessEvent event) {
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
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
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
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
}
