package com.phraseapp.androidstudio;

import java.io.File;

/**
 * Created by kolja on 13.10.15.
 */
public class ClientDetection {

    private static String[] knownInstallPaths = {
            "/usr/bin/phraseapp",
            "/usr/local/bin/phraseapp"
    };


    public static String findClientInstallation(){
        for(String path : knownInstallPaths){
            File f = new File(path);
            if (f.exists()){
                return path;
            }
        }
        return null;
    }
}
