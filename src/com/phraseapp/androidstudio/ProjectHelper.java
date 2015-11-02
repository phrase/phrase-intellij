package com.phraseapp.androidstudio;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VFileProperty;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by kevin on 26/10/15.
 */
public class ProjectHelper {


    public static void smartVisit(VirtualFile file, LinkedList<VirtualFile> fileList) {
        if (file.isDirectory() && !file.getName().startsWith(".") &&
                !file.is(VFileProperty.SYMLINK) && file.getChildren() != null) {
            for (VirtualFile childFile : file.getChildren()) {
                smartVisit(childFile, fileList);
            }
        } else if (!file.isDirectory()) {
            fileList.add(file);
        }
    }

    public static LinkedList<VirtualFile> findProjectLocales(VirtualFile resDir) {
        LinkedList<VirtualFile> locales = new LinkedList<VirtualFile>();
        LinkedList<VirtualFile> fileList = new LinkedList<VirtualFile>();
        smartVisit(resDir, fileList);
        for (VirtualFile file : fileList) {
            if (isLocaleFile(file)) {
                locales.add(file);
            }
        }
        return locales;
    }

    public static String getLocaleName(VirtualFile file) {
        String name = file.getParent().getName();
        String[] parts = name.split("-");
        if (parts.length <= 1) {
            return "";
        }

        ArrayList<String> list = new ArrayList<String>(Arrays.asList(parts));
        List<String> sublist = list.subList(1, list.size());
        return StringUtil.join(sublist, "-");
    }

    public static boolean isLocaleFile(VirtualFile file) {
        return file.getPath().contains("app/src/main/res/values-") && file.getName().equals("strings.xml");
    }

    @NotNull
    public static String getRelativPath(Project project, VirtualFile file) {
        String path = file.getPath();
        return path.substring(project.getBasePath().length(), path.length());
    }

}
