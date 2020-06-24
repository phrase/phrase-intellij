package com.phrase.intellij;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.phrase.intellij.ui.ColorTextPane;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kolja on 27.10.15.
 */
public class ToolWindowOutputWriter {

    private final Project project;
    private final SimpleDateFormat sdf;

    public ToolWindowOutputWriter(Project project) {
        this.project = project;
        this.sdf = new SimpleDateFormat("HH:mm:ss");
    }

    private void write(final String response, final ColorTextPane finalArea) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                finalArea.setEditable(true);
                finalArea.appendANSI(getFormattedTime() + response + "\n");
                finalArea.setEditable(false);
            }
        });
    }

    public void writeOutput(final String text) {
        final ToolWindowHelper outputWindowHelper = new ToolWindowHelper(project);
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                if (outputWindowHelper.getOutputWindow().isActive()) {
                    final ColorTextPane finalArea = outputWindowHelper.getColorTextPane();
                    outputWindowHelper.getOutputWindow().show(new Runnable() {
                        @Override
                        public void run() {
                            write(text, finalArea);
                        }
                    });
                } else {
                    outputWindowHelper.getOutputWindow().activate(new Runnable() {
                        @Override
                        public void run() {
                            write(text, outputWindowHelper.getColorTextPane());
                        }
                    });
                }
            }
        });


    }

    private String getFormattedTime() {
        return sdf.format(new Date()) + " ";
    }

}
