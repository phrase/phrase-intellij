package com.phraseapp.androidstudio;

import java.util.LinkedList;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VFileProperty;


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

    public static LinkedList<String> findProjectLocales(VirtualFile resDir) {
        LinkedList<String> locales = new LinkedList<String>();
        LinkedList<VirtualFile> fileList = new LinkedList<VirtualFile>();
        smartVisit(resDir, fileList);
        for (VirtualFile file : fileList) {
            if (isLocaleFile(file)) {
                locales.add(file.getPath());
            }
        }
        return locales;
    }

    public static String getLocaleCode(VirtualFile file) {
        String localecode[] = file.getParent().getName().split("-");
        return localecode.length == 3 ? localecode[1] + "-" + localecode[2] : localecode[1];
    }

    public static boolean isLocaleFile(VirtualFile file) {
        return file.getPath().contains("res/values-") && file.getName().equals("strings.xml");
    }

}
