package com.phraseapp.androidstudio;

import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by kolja on 21.10.15.
 */
public class PhraseAppConfiguration {
    private final Project project;

    public PhraseAppConfiguration(Project project){
        this.project = project;
    }

    public void setConfig(String s) {
        String projectPath = project.getBasePath();
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
}
