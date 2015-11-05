package com.phraseapp.androidstudio;

import com.intellij.ide.util.PropertiesComponent;


public class PropertiesRepository {
    public static final String PHRASEAPP_CLIENT_PATH = "com.phraseapp.client_path";

    private static PropertiesRepository instance;
    private PropertiesRepository() {}

    public static PropertiesRepository getInstance () {
        if (PropertiesRepository.instance == null) {
            PropertiesRepository.instance = new PropertiesRepository();
        }
        return PropertiesRepository.instance;
    }

    public String getClientPath() {
        return PropertiesComponent.getInstance().getValue(PHRASEAPP_CLIENT_PATH);
    }

    public void setClientPath(String path) {
        PropertiesComponent.getInstance().setValue(PHRASEAPP_CLIENT_PATH, path);
    }
}


