package com.phrase.intellij;

import java.io.File;

/**
 * Created by kolja on 13.10.15.
 */
public class ClientDetection {

    private static String[] knownInstallPaths = {
            "/usr/bin/phrase",
            "/usr/local/bin/phrase",
            System.getProperty("user.home") + "/bin/phrase",
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
