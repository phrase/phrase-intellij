package com.phraseapp.androidstudio;

import com.intellij.ide.util.PropertiesComponent;


public class PropertiesRepository {
    public static final String PHRASEAPP_CLIENT_PATH = "PHRASEAPP_CLIENT_PATH";
    public static final String PHRASEAPP_ACCESS_TOKEN = "PHRASEAPP_ACCESS_TOKEN";
    public static final String PHRASEAPP_DEFAULT_STRINGS_PATH = "DEFAULT_STRINGS_PATH";

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

    public String getDefaultStringsPath() {
        return PropertiesComponent.getInstance().getValue(PHRASEAPP_DEFAULT_STRINGS_PATH);
    }

    public void setDefaultStringsPath(String path) {
        PropertiesComponent.getInstance().setValue(PHRASEAPP_DEFAULT_STRINGS_PATH, path);
    }

    public String getAccessToken() {
        return PropertiesComponent.getInstance().getValue(PHRASEAPP_ACCESS_TOKEN);
    }

    public void setAccessToken(String accessToken) {
        PropertiesComponent.getInstance().setValue(PHRASEAPP_ACCESS_TOKEN, accessToken);
    }
}


