package com.phraseapp.androidstudio;

import com.intellij.ide.DataManager;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;


import java.io.*;
import java.util.Map;
import java.util.Scanner;


public class TokenRepository {
    public static final String PHRASEAPP_CLIENT_PATH = "PHRASEAPP_CLIENT_PATH";
    public static final String PHRASEAPP_ACCESS_TOKEN = "PHRASEAPP_ACCESS_TOKEN";
    public static final String PHRASEAPP_PROJECT_ID = "PHRASEAPP_PROJECT_ID";
    public static final String PHRASEAPP_UPDATE_TRANSLATIONS = "PHRASEAPP_UPDATE_TRANSLATIONS";
    public static final String PHRASEAPP_DEFAULT_STRINGS_PATH = "DEFAULT_STRINGS_PATH";
    public static final String PHRASEAPP_DEFAULT_LOCALE_PATH = "DEFAULT_LOCALE_PATH";


    public static class Holder {
        public static final TokenRepository HOLDER_INSTANCE = new TokenRepository();
    }

    public static TokenRepository getInstance() {
        return Holder.HOLDER_INSTANCE;
    }

    public void setDefaultLocale(String locale) {
        PropertiesComponent.getInstance().setValue(PHRASEAPP_DEFAULT_LOCALE_PATH, locale);
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

    public void setProjectId(String projectId) {
        PropertiesComponent.getInstance().setValue(PHRASEAPP_PROJECT_ID, projectId);
    }

    public boolean getUpdateTranslations() {
        return PropertiesComponent.getInstance().getBoolean(PHRASEAPP_UPDATE_TRANSLATIONS, false);
    }

    public void setUpdateTranslations(boolean update) {
        PropertiesComponent.getInstance().setValue(PHRASEAPP_UPDATE_TRANSLATIONS, Boolean.toString(update));
    }

    public void setConfig(String s) {
        String projectPath = getProjectPath();
        try {
            File configFile = new File(projectPath + "/.phraseapp.yml");
            System.out.println(configFile);
            FileUtils.writeStringToFile(configFile, s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateConfig(Map config) {
        Yaml yaml = new Yaml();
        StringWriter writer = new StringWriter();
        yaml.dump(config, writer);
        setConfig(writer.toString());
    }

    public String loadPhraseAppConfig() {
        String projectPath = getProjectPath();
        StringBuilder text = new StringBuilder();
        String NL = System.getProperty("line.separator");
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(projectPath + "/.phraseapp.yml"), "UTF-8");
        } catch (FileNotFoundException e) {
            return "";
        }
        try {
            while (scanner.hasNextLine()) {
                text.append(scanner.nextLine() + NL);
            }
        } finally {
            scanner.close();
        }

        return text.toString();
    }

    private String getProjectPath() {
        DataContext dataContext = DataManager.getInstance().getDataContext();
        Project project = (Project) dataContext.getData(DataConstants.PROJECT);
        return project.getBasePath();
    }
}


