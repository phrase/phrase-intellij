package com.phraseapp.androidstudio;

import com.intellij.ide.util.PropertiesComponent;

public class TokenRepository {
    public static final String PHRASEAPP_ACCESS_TOKEN = "PHRASEAPP_ACCESS_TOKEN";
    public static final String PHRASEAPP_PROJECT_ID = "PHRASEAPP_PROJECT_ID";


    public static class Holder {
        public static final TokenRepository HOLDER_INSTANCE = new TokenRepository();
    }

    public static TokenRepository getInstance() {
        return Holder.HOLDER_INSTANCE;
    }

    public String getAccessToken() {
        return PropertiesComponent.getInstance().getValue(PHRASEAPP_ACCESS_TOKEN);
    }

    public void setAccessToken(String accessToken) {
        PropertiesComponent.getInstance().setValue(PHRASEAPP_ACCESS_TOKEN, accessToken);
    }

    public String getProjectId() {
        return PropertiesComponent.getInstance().getValue(PHRASEAPP_PROJECT_ID);
    }

    public void setProjectId(String projectId) {
        PropertiesComponent.getInstance().setValue(PHRASEAPP_PROJECT_ID, projectId);
    }
}


