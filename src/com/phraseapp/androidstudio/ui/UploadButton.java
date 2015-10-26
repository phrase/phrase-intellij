package com.phraseapp.androidstudio.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        return file != null && file.getName().equals("strings.xml");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        String name = file.getParent().getName();
        String[] parts = name.split("-");
        if (parts.length <= 1) {
            return;
        }

        ArrayList<String> list = new ArrayList<String>(Arrays.asList(parts));
        List<String> sublist = list.subList(1, list.size());
        String localeName = StringUtil.join(sublist, "-");
        System.out.printf(localeName);
    }

}
