package com.phraseapp.androidstudio;

import com.intellij.ide.util.PropertiesComponent;

public class TokenRepository {
    public static final String PHRASE_AUTH_TOKEN_KEY = "PHRASE_AUTH_TOKEN_KEY";

    public static class Holder {
        public static final TokenRepository HOLDER_INSTANCE = new TokenRepository();
    }

    public static TokenRepository getInstance() {
        return Holder.HOLDER_INSTANCE;
    }

    public String getToken() {
        return PropertiesComponent.getInstance().getValue(PHRASE_AUTH_TOKEN_KEY);
    }

    public void setToken(String token) {
        PropertiesComponent.getInstance().setValue(PHRASE_AUTH_TOKEN_KEY, token);
    }
}


