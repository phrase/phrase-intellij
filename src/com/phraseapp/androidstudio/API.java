package com.phraseapp.androidstudio;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class API {

    public static final String PHRASEAPP_VERSION = "2.3";
    public static final String PHRASEAPP_API_BASEURL = "https://api.phraseapp.com/v2/";
    public static final String PHRASEAPP_USER_AGENT = "PhraseApp AndroidStudio " + PHRASEAPP_VERSION;
    private final String accessToken;
    private final String workingDir;

    public API(String access_token, String basePath) {
        this.accessToken = access_token;
        this.workingDir = basePath;
    }

    // Get all locales
    public APIResourceListModel getLocales(String projectId) {
        List<String> params = new ArrayList<String>();
        params.add(projectId);
        return runCommand("locales", "list", params);
    }


    // Get all projects
    public APIResourceListModel getProjects() {
        return runCommand("projects", "list", null);
    }

    // Create project
    public APIResourceListModel postProjects(String name) {
        List<String> params = new ArrayList<String>();
        params.add("--name");
        params.add(name);
        params.add("--main-format");
        params.add("xml");
        return runCommand("project", "create", params);
    }

    public APIResourceListModel postLocales(String projectId, String localeName) {
        List<String> params = new ArrayList<String>();
        params.add(projectId);
        params.add("--name");
        params.add(localeName);
        params.add("--code");
        params.add(localeName);
        return runCommand("locale", "create", params);
    }

    @Nullable
    private APIResourceListModel runCommand(String resource, String action, List<String> params) {
        GeneralCommandLine gcl = new GeneralCommandLine(PropertiesRepository.getInstance().getClientPath(),
                resource);
        gcl.addParameter(action);

        if (params != null) {
            gcl.addParameters(params);
        }

        gcl.addParameter("--access-token");
        gcl.addParameter(accessToken);

        gcl.withWorkDirectory(workingDir);
        System.out.printf(gcl.getCommandLineString());
        try {
            final CapturingProcessHandler processHandler = new CapturingProcessHandler(gcl.createProcess(), Charset.defaultCharset(), gcl.getCommandLineString());
            ProcessOutput output = processHandler.runProcess();
            String response = output.getStdout();
            System.out.printf(response);
            if (!response.isEmpty()) {
                APIResourceListModel resourceList = new APIResourceListModel();

                if (response.startsWith("[")) {
                    JSONArray objects = new JSONArray(response);


                    for (int i = 0; i < objects.length(); i++) {
                        JSONObject pro = (JSONObject) objects.get(i);
                        resourceList.addElement(new APIResource((String) pro.get("id"), (String) pro.get("name")));
                    }
                } else {
                    JSONObject object = new JSONObject(response);
                    resourceList.addElement(new APIResource((String) object.get("id"), (String) object.get("name")));
                }

                return resourceList;
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

}