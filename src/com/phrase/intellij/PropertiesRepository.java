package com.phrase.intellij;

import com.intellij.ide.util.PropertiesComponent;


public class PropertiesRepository {
    public static final String PHRASE_CLIENT_PATH = "com.phrase.client_path";

    private static PropertiesRepository instance;
    private PropertiesRepository() {}

    public static PropertiesRepository getInstance () {
        if (PropertiesRepository.instance == null) {
            PropertiesRepository.instance = new PropertiesRepository();
        }
        return PropertiesRepository.instance;
    }

    public String getClientPath() {
        return PropertiesComponent.getInstance().getValue(PHRASE_CLIENT_PATH);
    }

    public void setClientPath(String path) {
        PropertiesComponent.getInstance().setValue(PHRASE_CLIENT_PATH, path);
    }
}


