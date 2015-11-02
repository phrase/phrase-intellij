package com.phraseapp.androidstudio;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by kolja on 21.10.15.
 */
public class PhraseAppConfiguration {
    private final Project project;
    private String currentConfig;


    public PhraseAppConfiguration(Project project){
        this.project = project;
        this.currentConfig = loadPhraseAppConfig();
    }

    public void setConfig(String s) {
        String projectPath = project.getBasePath();
        try {
            File configFile = new File(projectPath + "/.phraseapp.yml");
            System.out.println(configFile);
            FileUtils.writeStringToFile(configFile, s);
            LocalFileSystem.getInstance().refreshIoFiles(Collections.singletonList(configFile));
            currentConfig = s;
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
        String projectPath = project.getBasePath();
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

    public String getProjectId() {
        String projectId = null;
        if (configExists()) {
            Yaml yaml = new Yaml();
            Map configYml = (Map) yaml.load(currentConfig);
            Map root = (Map) configYml.get("phraseapp");
            if (root != null) {
                projectId = (String) root.get("project_id");
            }
        }

        return projectId;
    }

    public String getAccessToken() {
        String accessToken = null;

        if (configExists()) {
            Yaml yaml = new Yaml();
            Map configYml = (Map) yaml.load(currentConfig);
            Map root = (Map) configYml.get("phraseapp");
            if (root != null) {
                accessToken = (String) root.get("access_token");
            }
        }

        return accessToken;
    }

    public String getLocaleId() {
        String localeId = null;

        if (configExists()) {
            Yaml yaml = new Yaml();
            Map configYml = (Map) yaml.load(currentConfig);
            Map root = (Map) configYml.get("phraseapp");
            if (root != null) {
                Map push = (Map) root.get("push");
                if (push != null) {
                    List<Map> sources = (List<Map>) push.get("sources");
                    Map source = sources.get(0);
                    Map params = (Map) source.get("params");
                    if (params != null){
                        localeId = (String) params.get("locale_id");
                    }
                }
            }
        }

        return localeId;
    }

    public boolean configExists() {
        return currentConfig.startsWith("phraseapp");
    }

    public boolean hasProjectId() {
        return getProjectId() != null;
    }

    public boolean hasAccessToken() {
        return getAccessToken() != null;
    }
}
