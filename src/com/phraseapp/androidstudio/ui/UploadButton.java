package com.phraseapp.androidstudio.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.phraseapp.androidstudio.*;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kolja on 26.10.15.
 */
public class UploadButton extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        final VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        final boolean isStringsXML = isStringsFile(file);
        e.getPresentation().setEnabled(isStringsXML);
        e.getPresentation().setVisible(isStringsXML);
    }

    private static boolean isStringsFile(@Nullable VirtualFile file) {
        return file != null && file.getName().equals("strings.xml") && !ProjectHelper.getLocaleName(file).isEmpty();
    }

    @Override
    public void actionPerformed(final AnActionEvent e) {
        final VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        final String localeName = ProjectHelper.getLocaleName(file);
        final ToolWindowOutputWriter outputWriter = new ToolWindowOutputWriter(e.getProject());
        outputWriter.writeOutput("Started Uploading locale: " + ColorTextPane.ANSI_GREEN + localeName + ColorTextPane.ANSI_STOP);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final API api = new API(PropertiesRepository.getInstance().getClientPath(), "123", e.getProject());

                api.postLocales(PropertiesRepository.getInstance().getProjectId(), localeName);
                APIResourceListModel upload = api.uploadLocale(
                        PropertiesRepository.getInstance().

                                getProjectId(),

                        ProjectHelper.getLocaleName(file),
                        file.getPath(),
                        "xml"
                );

                if (upload != null && !upload.isValid()) {
                    outputWriter.writeOutput(ColorTextPane.ANSI_RED + upload.getErrors() + ColorTextPane.ANSI_STOP);
                }

                outputWriter.writeOutput("Finished");
            }
        });

        thread.start();
    }

}
